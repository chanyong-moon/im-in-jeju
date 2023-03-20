package imin.jeju.iminjeju.adaptor.controller

import imin.jeju.iminjeju.dto.Location
import imin.jeju.iminjeju.port.LocationSearchPort
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/locations")
class LocationController(
    val locationSearchPort: LocationSearchPort
) {
    @GetMapping("/search")
    fun searchLocations(@RequestParam keyword: String): List<Location> {

        return locationSearchPort.search(keyword)
    }
}