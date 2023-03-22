package imin.jeju.iminjeju.port

import imin.jeju.iminjeju.dto.LocationDto

interface LocationCachePort {
    fun updateLocalCache()
    fun getCache(key: String): List<LocationDto>?
}