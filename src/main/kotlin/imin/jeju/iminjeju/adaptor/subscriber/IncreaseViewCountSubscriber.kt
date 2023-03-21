package imin.jeju.iminjeju.adaptor.subscriber

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Component

@Component
class IncreaseViewCountSubscriber : MessageListener {
    override fun onMessage(message: Message, pattern: ByteArray?) {
        
    }
}