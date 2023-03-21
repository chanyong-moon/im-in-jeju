package imin.jeju.iminjeju.api.port

import imin.jeju.iminjeju.api.dto.Location
import org.springframework.stereotype.Service

@Service
interface LocationSearchPort {
    fun search(keyword: String): List<Location>
}