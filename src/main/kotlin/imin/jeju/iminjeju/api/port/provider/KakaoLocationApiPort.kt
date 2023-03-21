package imin.jeju.iminjeju.api.port.provider

import imin.jeju.iminjeju.api.dto.KakaoLocationDto
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

interface KakaoLocationApiPort {
    @GetExchange("https://dapi.kakao.com/v2/local/search/keyword.json")
    fun request(
        @RequestHeader("Authorization") apiKey: String,
        @RequestParam size: Int,
        @RequestParam query: String): KakaoLocationDto
}