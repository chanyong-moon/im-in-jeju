package imin.jeju.iminjeju.port

import imin.jeju.iminjeju.dto.LocationDto

interface LocationProviderPort {
    fun locations(keyword: String): List<LocationDto>
}