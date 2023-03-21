package imin.jeju.iminjeju.domain.provider

import imin.jeju.iminjeju.dto.LocationDto
import imin.jeju.iminjeju.port.LocationProviderPort
import imin.jeju.iminjeju.port.provider.NaverLocationApiPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class NaverProvider(
    @Value("\${imin.jeju.api.provider.naver.request-size: 5}") val requestSize: Int,
    @Value("\${imin.jeju.api.provider.naver.api-key}") val apiKey: String,
    @Value("\${imin.jeju.api.provider.naver.secret}") val secret: String,
    val naverLocationApiPort: NaverLocationApiPort
) : LocationProviderPort {
    override fun locations(keyword: String): List<LocationDto> {
        return naverLocationApiPort.request(apiKey, secret, requestSize, keyword)
            .items
            .map { LocationDto(it.title, "naver") }
    }
}