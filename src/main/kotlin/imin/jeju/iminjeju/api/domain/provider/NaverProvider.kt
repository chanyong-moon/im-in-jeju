package imin.jeju.iminjeju.api.domain.provider

import imin.jeju.iminjeju.api.dto.Location
import imin.jeju.iminjeju.api.port.LocationProviderPort
import imin.jeju.iminjeju.api.port.provider.NaverLocationApiPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class NaverProvider(
    @Value("\${imin.jeju.api.provider.naver.request-size: 5}") val requestSize: Int,
    @Value("\${imin.jeju.api.provider.naver.api-key}") val apiKey: String,
    @Value("\${imin.jeju.api.provider.naver.secret}") val secret: String,
    val naverLocationApiPort: NaverLocationApiPort
) : LocationProviderPort {
    override fun locations(keyword: String): List<Location> {
        return naverLocationApiPort.request(apiKey, secret, requestSize, keyword)
            .items
            .map { Location(it.title, "naver") }
    }
}