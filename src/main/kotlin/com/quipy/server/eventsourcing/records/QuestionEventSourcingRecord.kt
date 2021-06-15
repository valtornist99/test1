package com.quipy.server.eventsourcing.records

import com.quipy.server.eventsourcing.EventSourcingRecord
import com.quipy.server.eventsourcing.entities.QuestionEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.QuestionUpdateEventName

data class QuestionEventSourcingRecord(
    override val entityId: String,
    override val correlationId: String? = null,
    override val eventTitle: QuestionUpdateEventName,
    override val payload: String
) : EventSourcingRecord<QuestionEventSourcingEntity, QuestionUpdateEventName>()