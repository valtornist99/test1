package com.quipy.server.eventsourcing.records

import com.quipy.server.eventsourcing.EventSourcingRecord
import com.quipy.server.eventsourcing.entities.TeamEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.TeamUpdateEventName

data class TeamEventSourcingRecord(
    override val entityId: String,
    override val correlationId: String? = null,
    override val eventTitle: TeamUpdateEventName,
    override val payload: String
) : EventSourcingRecord<TeamEventSourcingEntity, TeamUpdateEventName>()