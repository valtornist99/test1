package com.quipy.server.eventsourcing.records

import com.quipy.server.eventsourcing.EventSourcingRecord
import com.quipy.server.eventsourcing.entities.TaskEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.TaskUpdateEventName

data class TaskEventSourcingRecord(
    override val entityId: String,
    override val correlationId: String? = null,
    override val eventTitle: TaskUpdateEventName,
    override val payload: String
) : EventSourcingRecord<TaskEventSourcingEntity, TaskUpdateEventName>()