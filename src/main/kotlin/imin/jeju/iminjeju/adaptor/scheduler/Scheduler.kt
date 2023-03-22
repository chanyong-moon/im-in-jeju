package imin.jeju.iminjeju.adaptor.scheduler

import imin.jeju.iminjeju.port.LocationCachePort
import imin.jeju.iminjeju.port.LocationRankPort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Scheduler(
    val topSearchedViewCounterPort: LocationRankPort,
    val localCachePort: LocationCachePort,
) {
    @Scheduled(cron = "1 */\${imin.jeju.rank.minute-threshold} * * * *")
    fun renew() {
        topSearchedViewCounterPort.updateRanking()
        localCachePort.updateLocalCache()
    }
}