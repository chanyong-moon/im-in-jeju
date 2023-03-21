package imin.jeju.iminjeju.domain

import imin.jeju.iminjeju.port.TopSearchedLocationPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class TopSearchedLocationService(
    @Value("\${imin.jeju.integration.redis.pub-sub-channel}") val channel: String,
    val redisTemplate: RedisTemplate<String, Any>,
) : TopSearchedLocationPort {
    override fun increaseViewCount(keyword: String) {
        redisTemplate.convertAndSend(channel, "increase")
    }

    override fun findTopLocations() {
    }
}