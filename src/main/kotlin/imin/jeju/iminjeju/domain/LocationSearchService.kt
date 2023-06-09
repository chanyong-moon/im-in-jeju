package imin.jeju.iminjeju.domain

import imin.jeju.iminjeju.dto.LocationDto
import imin.jeju.iminjeju.extentions.trimTags
import imin.jeju.iminjeju.port.LocationSearchPort
import imin.jeju.iminjeju.port.LocationProviderPort
import imin.jeju.iminjeju.port.LocationRankPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatusCode
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class LocationSearchService(
    @Value("\${imin.jeju.search.max-result-size: 10}") val totalLocaionCount: Int,
    val providers: List<LocationProviderPort>,
    val topSearchedViewCounterPort: LocationRankPort,
) : LocationSearchPort {
    override fun search(keyword: String, increaseCount: Boolean): List<LocationDto> {
        // 검색
        val locationSetList = providers.map { provider ->
            return@map provider.locations(keyword)
                ?.onEach { location -> location.name = normalizeName(location.name) }
                ?.toSet()
        }

        val allProviderUnAvailable = locationSetList.all { it == null }
        if (allProviderUnAvailable) {
            throw ResponseStatusException(HttpStatusCode.valueOf(500), "모든 location provider를 이용할 수 없습니다.")
        }

        if (increaseCount) topSearchedViewCounterPort.increaseViewCount(keyword)
        return locations(locationSetList.filterNotNull().flatten())
    }

    private fun normalizeName(name: String): String = name.trim().trimTags()

    private fun locations(locationList: List<LocationDto>): List<LocationDto> {
        val commonLocationNames = findCommonLocationNames(locationList)

        val commonLocations = findCommonLocations(locationList.asSequence(), commonLocationNames)
        val restLocations = locationList.asSequence()
            .filter { it.name !in commonLocationNames }
            .take(totalLocaionCount - commonLocations.size)
            .toList()

        return commonLocations + restLocations
    }

    private fun findCommonLocations(locationList: Sequence<LocationDto>, commonLocationNames: Set<String>): List<LocationDto> {
        return locationList
            .filter { it.name in commonLocationNames }
            .distinctBy { it.name }
            .take(totalLocaionCount)
            .toList()
    }

    private fun findCommonLocationNames(locationList: List<LocationDto>): Set<String> {
        return locationList
            .groupingBy { it.name }
            .eachCount()
            .filter { it.value > 1 }
            .map { it.key }
            .toSet()
    }
}