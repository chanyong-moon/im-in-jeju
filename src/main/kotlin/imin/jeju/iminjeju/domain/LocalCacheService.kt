package imin.jeju.iminjeju.domain

import imin.jeju.iminjeju.dto.LocationDto
import imin.jeju.iminjeju.port.LocationCachePort
import imin.jeju.iminjeju.port.LocationRankPort
import imin.jeju.iminjeju.port.LocationSearchPort
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

const val CACHE_NAME = "location"

@Service
class LocalCacheService(
    val cacheManager: CacheManager,
    val locationRankPort: LocationRankPort,
    val locationSearchPort: LocationSearchPort,
) : LocationCachePort {
    override fun updateLocalCache() {
        val cache = cacheManager.getCache(CACHE_NAME)

        locationRankPort.findTopSearchedKeywords()
            .map { it.keyword }
            .forEach { keyword ->
                cache?.put(keyword, locationSearchPort.search(keyword, false)?.toTypedArray())
                println(cache?.get(keyword, Array<LocationDto>::class.java)?.toList())
            }
    }

    override fun getCache(key: String): List<LocationDto>? {
        return cacheManager.getCache(CACHE_NAME)?.get(key, Array<LocationDto>::class.java)?.toList()
    }
}