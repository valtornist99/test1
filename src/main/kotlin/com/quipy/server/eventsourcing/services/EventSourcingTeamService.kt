package com.quipy.server.eventsourcing.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.eventbus.EventBus
import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.entities.TeamEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.TeamUpdateEventName
import com.quipy.server.eventsourcing.records.TeamEventSourcingRecord
import com.quipy.server.eventsourcing.repositories.TeamEventsRepository
import com.quipy.server.eventsourcing.turnToEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventSourcingTeamService {
    @Autowired
    lateinit var cacheUpdateEventBus: EventBus

    @Autowired
    lateinit var teamEventsRepository: TeamEventsRepository

    @Autowired
    lateinit var jsonObjectMapper: ObjectMapper

    fun get(teamId: UUID): TeamEventSourcingEntity {
        val teamRecords = teamEventsRepository.findAllByEntityId(teamId.toString())

        if (teamRecords.isEmpty()) {
            throw IllegalArgumentException("No team with id $teamId")
        }

        return teamRecords.turnToEntity(teamId.toString()) {
            jsonObjectMapper.getUpdateEvent()
        }
    }

    fun update(event: UpdateEvent<TeamEventSourcingEntity, TeamUpdateEventName>) {
        val teamEvent = TeamEventSourcingRecord(
            entityId = event.entityId.toString(),
            correlationId = event.correlationId,
            eventTitle = event.name,
            payload = jsonObjectMapper.writeValueAsString(event)
        )

        teamEventsRepository.save(teamEvent)

        cacheUpdateEventBus.post(this.get(event.entityId))

        println("Team updated, cache event published $event")
    }
}