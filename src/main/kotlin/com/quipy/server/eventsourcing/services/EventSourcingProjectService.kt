package com.quipy.server.eventsourcing.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.EventBus
import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.entities.ProjectEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.ProjectUpdateEventName
import com.quipy.server.eventsourcing.records.ProjectEventSourcingRecord
import com.quipy.server.eventsourcing.repositories.ProjectEventsRepository
import com.quipy.server.eventsourcing.turnToEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventSourcingProjectService {
    @Autowired
    lateinit var cacheUpdateEventBus: EventBus

    @Autowired
    lateinit var projectEventsRepository: ProjectEventsRepository

    @Autowired
    lateinit var jsonObjectMapper: ObjectMapper

    fun get(projectId: UUID): ProjectEventSourcingEntity {
        val projectRecords = projectEventsRepository.findAllByEntityId(projectId.toString())

        if (projectRecords.isEmpty()) {
            throw IllegalArgumentException("No project with id $projectId")
        }

        return projectRecords.turnToEntity(projectId.toString()) {
            jsonObjectMapper.getUpdateEvent()
        }
    }

    fun update(event: UpdateEvent<ProjectEventSourcingEntity, ProjectUpdateEventName>) {
        val projectEvent = ProjectEventSourcingRecord(
            entityId = event.entityId.toString(),
            correlationId = event.correlationId,
            eventTitle = event.name,
            payload = jsonObjectMapper.writeValueAsString(event)
        )

        projectEventsRepository.save(projectEvent)

        cacheUpdateEventBus.post(this.get(event.entityId))

        println("Project updated, cache event published $event")
    }
}