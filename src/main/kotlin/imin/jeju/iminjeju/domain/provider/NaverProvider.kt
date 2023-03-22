package imin.jeju.iminjeju.domain.provider

import imin.jeju.iminjeju.dto.LocationDto
import imin.jeju.iminjeju.port.LocationProviderPort
import imin.jeju.iminjeju.adaptor.provider.NaverLocationApiPort
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}
@Component
class NaverProvider(
    @Value("\${imin.jeju.search.provider.naver.request-size: 5}") val requestSize: Int,
    @Value("\${imin.jeju.search.provider.naver.api-key}") val apiKey: String,
    @Value("\${imin.jeju.search.provider.naver.secret}") val secret: String,
    val naverLocationApiPort: NaverLocationApiPort
) : LocationProviderPort {
    override fun locations(keyword: String): List<LocationDto>? {
        return try {
            naverLocationApiPort.request(apiKey, secret, requestSize, keyword)
                .items
                .map { LocationDto(it.title, "naver") }
        } catch(e: Throwable) {
            logger.error(e) {}
            null
        }
    }
}