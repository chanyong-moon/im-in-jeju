package imin.jeju.iminjeju.port

interface TopSearchedLocationPort {
    fun increaseViewCount(keyword: String)

    fun findTopLocations()
}