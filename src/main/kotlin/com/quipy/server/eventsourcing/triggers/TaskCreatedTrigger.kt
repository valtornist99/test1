package com.quipy.server.eventsourcing.triggers

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.quipy.server.bot.QuipyBot
import com.quipy.server.eventsourcing.api.events.incoming.AddCommentQuestionEvent
import com.quipy.server.eventsourcing.api.events.incoming.CreateTaskEvent
import com.quipy.server.eventsourcing.services.EventSourcingQuestionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class TaskCreatedTrigger {
    @Autowired
    lateinit var updateEventTriggersEventBus: EventBus

    @Autowired
    lateinit var eventSourcingQuestionService: EventSourcingQuestionService

    @PostConstruct
    fun init() {
        updateEventTriggersEventBus.register(this)
    }

    @Subscribe
    fun addCommentToThreadAboutTask(createTaskEvent: CreateTaskEvent) {
        if (createTaskEvent.associatedQuestionId == null)
            return

        eventSourcingQuestionService.update(
            AddCommentQuestionEvent(
                entityId = UUID.fromString(createTaskEvent.associatedQuestionId),
                correlationId = createTaskEvent.correlationId,
                commentId = UUID.randomUUID().toString(),
                commentContent = "Task was grown out of the question",
                authorId = UUID.fromString(QuipyBot.botUserUuid),
                taskId = createTaskEvent.entityId
            )
        )
    }
}