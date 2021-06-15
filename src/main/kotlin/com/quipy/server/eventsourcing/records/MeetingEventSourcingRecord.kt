package com.quipy.server.eventsourcing.records

import com.quipy.server.eventsourcing.EventSourcingRecord
import com.quipy.server.eventsourcing.entities.MeetingEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.MeetingUpdateEventName

data class MeetingEventSourcingRecord(
    override val entityId: String,
    override val correlationId: String? = null,
    override val eventTitle: MeetingUpdateEventName,
    override val payload: String
) : EventSourcingRecord<MeetingEventSourcingEntity, MeetingUpdateEventName>()