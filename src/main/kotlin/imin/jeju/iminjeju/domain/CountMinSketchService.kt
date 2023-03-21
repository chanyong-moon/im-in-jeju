package imin.jeju.iminjeju.domain

import imin.jeju.iminjeju.dto.RankDto
import imin.jeju.iminjeju.port.TopSearchedViewCounterPort
import imin.jeju.iminjeju.util.HashFunction
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

const val TOTAL_COUNT_SKETCH = "total:sketch"

@Service
class CountMinSketchService(
    @Value("\${imin.jeju.rank.depth}") val depth: Int,
    @Value("\${imin.jeju.rank.width}") val width: Int,
    @Value("\${imin.jeju.rank.cache-times}") val cacheTimes: Int,
    @Value("\${imin.jeju.rank.minute-threshold}") val minuteThreshold: Int,
    @Value("\${imin.jeju.rank.ranking-size}") val rankingSize: Long,
    val cmsRedisTemplate: RedisTemplate<String, Int>,
    val redisTemplate: RedisTemplate<String, String>,
) : TopSearchedViewCounterPort {
    private val hashFunction: HashFunction = HashFunction()

    override fun increaseViewCount(keyword: String): Long {
        val hashes = hashFunction.hash(keyword, depth, width)
        var cnt = Long.MAX_VALUE

        val indexes = (0 until depth).map { it to hashes[it] }

        with(indexes) {
            // update total sketch
            map { "$TOTAL_COUNT_SKETCH:${it.first}:${it.second}" }
                .forEach {
                    cnt = minOf(cmsRedisTemplate.opsForValue().increment(it) ?: 1, cnt)
                }

            // update each sketch
            val sketch = sketchName()
            forEach { (i, j) ->
                cmsRedisTemplate.opsForValue().increment("${sketch}:diff:$i:$j")
            }
        }

        // update total:rank
        updateRank(keyword, cnt)

        // {sketchName}:rank
        updateRank(keyword, cnt, sketchName())

        return cnt
    }

    private fun updateRank(keyword: String, cnt: Long, sketch: String = "total") {
        val rankKey = "${sketch}:rank"

        with(redisTemplate.opsForZSet()) {
            add(rankKey, keyword, cnt.toDouble())
            val size = zCard(rankKey) ?: 0
            if (size > rankingSize) {
                removeRange(rankKey, 0, size - rankingSize)
            }
        }
    }

    private fun sketchName(time: LocalDateTime = LocalDateTime.now()): String {
        val minute = time.minute
        val remainder = minute % minuteThreshold
        val previousMinute = minute - remainder
        return time.withMinute(previousMinute).withSecond(0).withNano(0).toString()
    }

    override fun findTopSearchedKeywords(): List<RankDto> {
        val key = "total:rank"

        return (redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, rankingSize) as Set<ZSetOperations.TypedTuple<String>>)
            .mapNotNull { tup -> if (tup.value != null && tup.score != null) RankDto(tup.value!!, tup.score!!.toLong()) else null }
    }

    @Scheduled(cron = "1 */\${imin.jeju.rank.minute-threshold} * * * *")
    fun updateSketch() {
        val removeDiff = "${sketchName(LocalDateTime.now().minusMinutes(minuteThreshold.toLong() * (cacheTimes)))}:diff"

        // update sketch
        with(cmsRedisTemplate.opsForValue()) {
            for (i in 0 until depth) {
                val keys = (0 until width).map { "$removeDiff:$i:$it" }
                val minus = multiGet(keys) ?: ((0 until width).map { 0 })

                (0 until width).forEach {
                    decrement("$TOTAL_COUNT_SKETCH:$i:$it", (minus.getOrNull(it)?.toLong() ?: 0L))
                }
            }
        }

        // update rank
        (0 until cacheTimes)
            .map { LocalDateTime.now().minusMinutes(minuteThreshold.toLong() * it) }
            .map { "${sketchName(it)}:rank" }
            .map { key -> redisTemplate.opsForZSet().reverseRange(key, 0, rankingSize) as? Set<String> ?: listOf() }
            .flatten()
            .distinct()
            .map { keyword -> keyword to getViewCount(keyword) }
            .sortedByDescending { it.second }
            .take(rankingSize.toInt())
            .toList()
            .apply {
                // 전체 rank에서 지워야 할 키워드
                val deleteKeyword = redisTemplate.opsForZSet()
                    .range("total:rank", 0, Long.MAX_VALUE)!!
                    .subtract(map { it.first }.toSet())

                if (deleteKeyword.isNotEmpty()) redisTemplate.opsForZSet().remove("total:rank", *deleteKeyword.toTypedArray())

                forEach { (keyword, cnt) ->
                    updateRank(keyword, cnt)
                }
            }

    }

    private fun getViewCount(keyword: String): Long {
        val hashes = hashFunction.hash(keyword, depth, width)
        val indexes = (0 until depth).map { it to hashes[it] }

        return with(indexes) {
            // update total sketch
            val counts = (cmsRedisTemplate.opsForValue().multiGet(map { "$TOTAL_COUNT_SKETCH:${it.first}:${it.second}" }) as? List<Long>) ?: map { 0L }
            counts.min()
        }
    }
}