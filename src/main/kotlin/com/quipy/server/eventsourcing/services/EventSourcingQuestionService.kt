package com.quipy.server.eventsourcing.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.EventBus
import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.entities.QuestionEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.QuestionUpdateEventName
import com.quipy.server.eventsourcing.records.QuestionEventSourcingRecord
import com.quipy.server.eventsourcing.repositories.QuestionEventsRepository
import com.quipy.server.eventsourcing.turnToEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventSourcingQuestionService {
    @Autowired
    lateinit var cacheUpdateEventBus: EventBus

    @Autowired
    lateinit var questionEventsRepository: QuestionEventsRepository

    @Autowired
    lateinit var jsonObjectMapper: ObjectMapper

    @Autowired
    lateinit var updateEventTriggersEventBus: EventBus

    fun get(questionId: UUID, lastEventId: UUID? = null): QuestionEventSourcingEntity {
        val questionRecords = questionEventsRepository.findAllByEntityId(questionId.toString())

        if (questionRecords.isEmpty()) {
            throw IllegalArgumentException("No question with id $questionId")
        }

        return questionRecords.turnToEntity(entityId = questionId.toString(), upToEvent = lastEventId) {
            jsonObjectMapper.getUpdateEvent()
        }
    }

    fun update(event: UpdateEvent<QuestionEventSourcingEntity, QuestionUpdateEventName>) {
        val questionEvent = QuestionEventSourcingRecord(
            entityId = event.entityId.toString(),
            correlationId = event.correlationId,
            eventTitle = event.name,
            payload = jsonObjectMapper.writeValueAsString(event)
        )

        questionEventsRepository.save(questionEvent)

        cacheUpdateEventBus.post(this.get(event.entityId))
        updateEventTriggersEventBus.post(event)
        println("Question updated, cache event published $event")
    }
}