package com.quipy.server.eventsourcing.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.EventBus
import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.entities.TaskEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.TaskUpdateEventName
import com.quipy.server.eventsourcing.records.TaskEventSourcingRecord
import com.quipy.server.eventsourcing.repositories.TaskEventsRepository
import com.quipy.server.eventsourcing.turnToEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventSourcingTaskService {
    @Autowired
    lateinit var cacheUpdateEventBus: EventBus

    @Autowired
    lateinit var updateEventTriggersEventBus: EventBus

    @Autowired
    lateinit var taskEventsRepository: TaskEventsRepository

    @Autowired
    lateinit var jsonObjectMapper: ObjectMapper

    fun get(projectId: UUID): TaskEventSourcingEntity {
        val projectRecords = taskEventsRepository.findAllByEntityId(projectId.toString())

        if (projectRecords.isEmpty()) {
            throw IllegalArgumentException("No task with id $projectId")
        }

        return projectRecords.turnToEntity(projectId.toString()) {
            jsonObjectMapper.getUpdateEvent()
        }
    }

    fun update(event: UpdateEvent<TaskEventSourcingEntity, TaskUpdateEventName>) {
        val taskEvent = TaskEventSourcingRecord(
            entityId = event.entityId.toString(),
            correlationId = event.correlationId,
            eventTitle = event.name,
            payload = jsonObjectMapper.writeValueAsString(event)
        )

        taskEventsRepository.save(taskEvent)

        updateEventTriggersEventBus.post(event)
        cacheUpdateEventBus.post(this.get(event.entityId))

        println("Task updated, cache event published $event")
    }
}