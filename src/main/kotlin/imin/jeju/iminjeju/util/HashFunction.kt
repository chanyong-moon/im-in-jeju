package imin.jeju.iminjeju.util

class HashFunction {
    fun hash(item: String, count: Int, size: Int): IntArray {
        val hash = jenkinsHash(item)
        val hashes = IntArray(count)
        for (i in 0 until count) {
            hashes[i] = (hash + i) % size
        }

        return hashes
    }

    /**
     * MurmurHash: 빠른속도와 좋은 분포성을 가지지만 충돌이 적음
     * JenkinsHash: 느리지만 더 낮은 해시 충돌 확률을 보장 => eventual consistent라고 생각하고 오차확률을 더 줄이기위해 선택
     */
    private fun jenkinsHash(key: String): Int {
        var hash = 0
        for (ch in key) {
            hash += ch.code
            hash += (hash shl 10)
            hash = hash xor (hash shr 6)
        }
        hash += (hash shl 3)
        hash = hash xor (hash shr 11)
        hash += (hash shl 15)
        return hash and Int.MAX_VALUE
    }
}