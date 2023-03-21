package imin.jeju.iminjeju.counter.port

import imin.jeju.iminjeju.api.dto.Rank

interface TopSearchedViewCounterPort {
    fun increaseViewCount(keyword: String): Long

    fun findTopSearchedKeywords(): List<Rank>
}