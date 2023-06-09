package imin.jeju.iminjeju.adaptor.controller

import imin.jeju.iminjeju.dto.LocationDto
import imin.jeju.iminjeju.dto.RankDto
import imin.jeju.iminjeju.port.LocationCachePort
import imin.jeju.iminjeju.port.LocationSearchPort
import imin.jeju.iminjeju.port.LocationRankPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/locations")
class LocationController(
    val locationSearchPort: LocationSearchPort,
    val topSearchedViewCounterPort: LocationRankPort,
    val localCachePort: LocationCachePort,
    val locationRankPort: LocationRankPort,
) {
    @GetMapping("/search")
    fun searchLocations(@RequestParam keyword: String): List<LocationDto> {
        return localCachePort.getCache(keyword)?.apply { locationRankPort.increaseViewCount(keyword) } ?: locationSearchPort.search(keyword, true)
    }

    @GetMapping("/rank")
    fun getRank(): List<RankDto> {
        return topSearchedViewCounterPort.findTopSearchedKeywords()
    }
}