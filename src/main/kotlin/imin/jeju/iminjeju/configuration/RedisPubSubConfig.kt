package imin.jeju.iminjeju.configuration

import imin.jeju.iminjeju.adaptor.subscriber.IncreaseViewCountSubscriber
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter


@Configuration
class RedisPubSubConfig {
    @Bean
    fun redisMessageListenerContainer(
        @Value("\${imin.jeju.integration.redis.pub-sub-channel}") channel: String,
        redisConnectionFactory: RedisConnectionFactory,
        messageListenerAdapter: MessageListenerAdapter,
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        container.addMessageListener(messageListenerAdapter, ChannelTopic(channel))
        return container
    }

    @Bean
    fun messageListenerAdapter(increaseViewCountSubscriber: IncreaseViewCountSubscriber): MessageListenerAdapter? {
        return MessageListenerAdapter(increaseViewCountSubscriber)
    }
}