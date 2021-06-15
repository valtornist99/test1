package com.quipy.server.eventsourcing.triggers

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.quipy.server.eventsourcing.api.events.outgoing.CommentCreatedEvent
import com.quipy.server.eventsourcing.api.events.incoming.AddCommentQuestionEvent
import com.quipy.server.eventsourcing.services.EventSourcingQuestionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.*
import javax.annotation.PostConstruct

@Component
class CommentCreatedTrigger {
    @Autowired
    lateinit var updateEventTriggersEventBus: EventBus

    @Autowired
    lateinit var domainEventsEventBus: EventBus

    @Autowired
    lateinit var eventSourcingQuestionService: EventSourcingQuestionService

    @PostConstruct
    fun init() {
        updateEventTriggersEventBus.register(this)
    }

    @Subscribe
    fun notifyAllAboutComment(addCommentEvent: AddCommentQuestionEvent) {
        // getting the entity of exact same type that event has
        val question = eventSourcingQuestionService.get(addCommentEvent.entityId, UUID.fromString(addCommentEvent.id))

        val commentCreatedDomainEvent = CommentCreatedEvent(
            projectId = addCommentEvent.correlationId,
            questionId = addCommentEvent.entityId.toString(),
            authorId = addCommentEvent.authorId.toString(),
            commentId = addCommentEvent.commentId,
            totalNumberOfComments = question.comments.size,
            questionWatchers = question.watchers,
            questionVersion = question.version
        )

        domainEventsEventBus.post(commentCreatedDomainEvent)
    }
}