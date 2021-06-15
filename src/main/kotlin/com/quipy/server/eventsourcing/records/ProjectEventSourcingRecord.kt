package com.quipy.server.eventsourcing.records

import com.quipy.server.eventsourcing.EventSourcingRecord
import com.quipy.server.eventsourcing.entities.ProjectEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.ProjectUpdateEventName

data class ProjectEventSourcingRecord(
    override val entityId: String,
    override val correlationId: String? = null,
    override val eventTitle: ProjectUpdateEventName,
    override val payload: String
) : EventSourcingRecord<ProjectEventSourcingEntity, ProjectUpdateEventName>()