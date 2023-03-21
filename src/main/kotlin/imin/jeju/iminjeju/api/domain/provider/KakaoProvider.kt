package imin.jeju.iminjeju.api.domain.provider

import imin.jeju.iminjeju.api.dto.Location
import imin.jeju.iminjeju.api.port.provider.KakaoLocationApiPort
import imin.jeju.iminjeju.api.port.LocationProviderPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KakaoProvider(
    @Value("\${imin.jeju.api.provider.kakao.request-size: 5}") val requestSize: Int,
    @Value("\${imin.jeju.api.provider.kakao.api-key}") val apiKey: String,
    val kakaoLocationApiPort: KakaoLocationApiPort
) : LocationProviderPort {
    override fun locations(keyword: String): List<Location> {
        return kakaoLocationApiPort.request(apiKey, requestSize, keyword)
            .documents
            .map {
                with(it) {
                    Location(name, "kakao")
                }
            }
    }
}