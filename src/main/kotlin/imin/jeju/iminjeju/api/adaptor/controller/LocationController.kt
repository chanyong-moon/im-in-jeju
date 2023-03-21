package imin.jeju.iminjeju.api.adaptor.controller

import imin.jeju.iminjeju.api.dto.Location
import imin.jeju.iminjeju.api.dto.Rank
import imin.jeju.iminjeju.api.port.LocationSearchPort
import imin.jeju.iminjeju.counter.port.TopSearchedViewCounterPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/locations")
class LocationController(
    val locationSearchPort: LocationSearchPort,
    val topSearchedViewCounterPort: TopSearchedViewCounterPort,
) {
    @GetMapping("/search")
    fun searchLocations(@RequestParam keyword: String): List<Location> {
        return locationSearchPort.search(keyword)
    }

    @GetMapping("/rank")
    fun getRank(): List<Rank> {
        return topSearchedViewCounterPort.findTopSearchedKeywords()
    }
}