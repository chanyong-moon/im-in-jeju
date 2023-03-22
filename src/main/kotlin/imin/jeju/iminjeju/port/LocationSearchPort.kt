package imin.jeju.iminjeju.port

import imin.jeju.iminjeju.dto.LocationDto
import org.springframework.stereotype.Service

@Service
interface LocationSearchPort {
    fun search(keyword: String, increaseCount: Boolean): List<LocationDto>
}