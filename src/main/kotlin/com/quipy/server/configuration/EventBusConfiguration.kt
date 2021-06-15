package com.quipy.server.configuration

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

@Configuration
class EventBusConfiguration {
    companion object {
        val logger = LoggerFactory.getLogger(EventBusConfiguration::class.java)
    }

    private val cacheUpdateExecutor = Executors.newFixedThreadPool(
        4,
        TFactory("cache_update_async_bus")
    ) // todo sukhoa bounded queue + rejection handler

    private val updateEventTriggerExecutor = Executors.newFixedThreadPool(
        4,
        TFactory("update_event_triggers_async_bus")
    )

    private val domainEventsExecutor = Executors.newFixedThreadPool(
        4,
        TFactory("domain_events_async_bus")
    )

    @Bean
    fun cacheUpdateEventBus(): EventBus = AsyncEventBus("cache_update_async_bus", cacheUpdateExecutor) // todo change name as it's used not only for cache

    @Bean
    fun updateEventTriggersEventBus(): EventBus =
        AsyncEventBus("update_event_triggers_async_bus", updateEventTriggerExecutor)

    @Bean
    fun domainEventsEventBus(): EventBus =
        AsyncEventBus("domain_events_async_bus", domainEventsExecutor)
}

internal class TFactory(
    private val prefix: String
) : ThreadFactory {
    private val threadFactory = Executors.defaultThreadFactory()

    override fun newThread(r: Runnable): Thread {
        val thread = threadFactory.newThread(r)
        thread.name = "$prefix-${thread.name}"
        return thread
    }
}