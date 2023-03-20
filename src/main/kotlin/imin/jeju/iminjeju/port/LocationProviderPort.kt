package imin.jeju.iminjeju.port

import imin.jeju.iminjeju.dto.Location

interface LocationProviderPort {
    fun locations(keyword: String): List<Location>
}