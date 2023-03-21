package imin.jeju.iminjeju.api.port

import imin.jeju.iminjeju.api.dto.Location

interface LocationProviderPort {
    fun locations(keyword: String): List<Location>
}