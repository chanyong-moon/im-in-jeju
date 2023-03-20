package imin.jeju.iminjeju.domain

import imin.jeju.iminjeju.port.LocationSearchPort
import imin.jeju.iminjeju.port.ProviderPort
import org.springframework.stereotype.Service

@Service
class LocationSearchService(
    val providers: List<ProviderPort>
) : LocationSearchPort {
    fun search(keyword: String) {
        // 검색
        // viewcount 증가 # redis
    }

    private fun extractCommonLocation() {
    }

    /**
     * Increase view count
     * ChainedTransactionManager 사용
     */
    private fun increaseViewCount() {
    }
}