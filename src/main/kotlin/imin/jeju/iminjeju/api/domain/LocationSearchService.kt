package imin.jeju.iminjeju.api.domain

import imin.jeju.iminjeju.api.dto.Location
import imin.jeju.iminjeju.extentions.trimTags
import imin.jeju.iminjeju.api.port.LocationSearchPort
import imin.jeju.iminjeju.api.port.LocationProviderPort
import imin.jeju.iminjeju.counter.port.TopSearchedViewCounterPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class LocationSearchService(
    @Value("\${imin.jeju.api.total-search-result-size: 10}") val totalLocaionCount: Int,
    val providers: List<LocationProviderPort>,
    val topSearchedViewCounterPort: TopSearchedViewCounterPort,
) : LocationSearchPort {
    override fun search(keyword: String): List<Location> {
        // 검색
        val locationSetList = providers.map { provider ->
            return@map provider.locations(keyword)
                .onEach { location -> location.name = normalizeName(location.name) }
                .toSet()
        }

        // increaseViewCount
        // viewcount 증가 # redis
        topSearchedViewCounterPort.increaseViewCount(keyword)
        return locations(locationSetList.flatten())
    }

    private fun normalizeName(name: String): String = name.trim().trimTags()

    private fun locations(locationList: List<Location>): List<Location> {
        val commonLocationNames = findCommonLocationNames(locationList)

        val commonLocations = findCommonLocations(locationList.asSequence(), commonLocationNames)
        val restLocations = locationList.asSequence()
            .filter { it.name !in commonLocationNames }
            .take(totalLocaionCount - commonLocations.size)
            .toList()

        return commonLocations + restLocations
    }

    private fun findCommonLocations(locationList: Sequence<Location>, commonLocationNames: Set<String>): List<Location> {
        return locationList
            .filter { it.name in commonLocationNames }
            .distinctBy { it.name }
            .take(totalLocaionCount)
            .toList()
    }

    private fun findCommonLocationNames(locationList: List<Location>): Set<String> {
        return locationList
            .groupingBy { it.name }
            .eachCount()
            .filter { it.value > 1 }
            .map { it.key }
            .toSet()
    }
}