package com.quipy.server.eventsourcing.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.EventBus
import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.entities.MeetingEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.MeetingUpdateEventName
import com.quipy.server.eventsourcing.records.MeetingEventSourcingRecord
import com.quipy.server.eventsourcing.repositories.MeetingEventsRepository
import com.quipy.server.eventsourcing.turnToEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventSourcingMeetingService {
    @Autowired
    lateinit var cacheUpdateEventBus: EventBus

    @Autowired
    lateinit var meetingEventsRepository: MeetingEventsRepository

    @Autowired
    lateinit var jsonObjectMapper: ObjectMapper

    fun get(meetingId: UUID): MeetingEventSourcingEntity {
        val meetingRecords = meetingEventsRepository.findAllByEntityId(meetingId.toString())

        if (meetingRecords.isEmpty()) {
            throw IllegalArgumentException("No meeting with id $meetingId")
        }

        return meetingRecords.turnToEntity(meetingId.toString()) {
            jsonObjectMapper.getUpdateEvent()
        }
    }

    fun update(event: UpdateEvent<MeetingEventSourcingEntity, MeetingUpdateEventName>) {
        val taskEvent = MeetingEventSourcingRecord(
            entityId = event.entityId.toString(),
            correlationId = event.correlationId,
            eventTitle = event.name,
            payload = jsonObjectMapper.writeValueAsString(event)
        )

        meetingEventsRepository.save(taskEvent)

        cacheUpdateEventBus.post(this.get(event.entityId))

        println("Meeting updated, cache event published $event")
    }
}