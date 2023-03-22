package imin.jeju.iminjeju.configuration

import imin.jeju.iminjeju.adaptor.provider.KakaoLocationApiPort
import imin.jeju.iminjeju.adaptor.provider.NaverLocationApiPort
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory


@Configuration
class HttpInterfaceConfig {
    @Bean
    fun messageConverter(): KotlinSerializationJsonDecoder {
        return KotlinSerializationJsonDecoder(Json {
            ignoreUnknownKeys = true
        })
    }

    @Bean
    fun client(): WebClient {
        return WebClient.builder()
            .codecs {
                it.defaultCodecs().kotlinSerializationJsonDecoder(
                    KotlinSerializationJsonDecoder(Json {
                        ignoreUnknownKeys = true
                    })
                )
            }
            .build()
    }

    @Bean
    fun httpServiceProxyFactory(client: WebClient): HttpServiceProxyFactory? {
        return HttpServiceProxyFactory
            .builder(WebClientAdapter.forClient(client))
            .build()
    }

    @Bean
    fun kakaoLocationApiPort(httpServiceProxyFactory: HttpServiceProxyFactory): KakaoLocationApiPort {
        return httpServiceProxyFactory.createClient(KakaoLocationApiPort::class.java)
    }

    @Bean
    fun naverLocationApiPort(httpServiceProxyFactory: HttpServiceProxyFactory): NaverLocationApiPort {
        return httpServiceProxyFactory.createClient(NaverLocationApiPort::class.java)
    }
}