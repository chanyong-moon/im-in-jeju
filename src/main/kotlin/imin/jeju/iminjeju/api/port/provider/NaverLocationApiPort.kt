package imin.jeju.iminjeju.api.port.provider

import imin.jeju.iminjeju.api.dto.NaverLocationDto
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

interface NaverLocationApiPort {
    @GetExchange("https://openapi.naver.com/v1/search/local.json")
    fun request(
        @RequestHeader("X-Naver-Client-Id") apiKey: String,
        @RequestHeader("X-Naver-Client-Secret") secret: String,
        @RequestParam("display") size: Int,
        @RequestParam query: String): NaverLocationDto
}