package imin.jeju.iminjeju.port

import imin.jeju.iminjeju.dto.RankDto

interface LocationRankPort {
    fun increaseViewCount(keyword: String): Long
    fun findTopSearchedKeywords(): List<RankDto>
    fun updateRanking()
}