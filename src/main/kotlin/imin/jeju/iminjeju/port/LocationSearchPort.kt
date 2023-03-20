package imin.jeju.iminjeju.port

import imin.jeju.iminjeju.dto.Location
import org.springframework.stereotype.Service

@Service
interface LocationSearchPort {
    fun search(keyword: String): List<Location>
}