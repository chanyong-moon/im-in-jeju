package imin.jeju.iminjeju.configuration

import imin.jeju.iminjeju.api.domain.provider.KakaoProvider
import imin.jeju.iminjeju.api.domain.provider.NaverProvider
import imin.jeju.iminjeju.api.port.LocationProviderPort
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@Configuration
class ApplicationConfig {
    @Bean
    fun locationProviders(
        kakaoProvider: KakaoProvider,
        naverPrvider: NaverProvider
    ): List<LocationProviderPort> {
        // 우선순위대로 추가
        return listOf(kakaoProvider, naverPrvider)
    }
}