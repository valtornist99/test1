package com.quipy.server.eventsourcing.api.events.incoming

import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.UpdateEventName
import com.quipy.server.eventsourcing.entities.MeetingEventSourcingEntity
import java.util.*
import kotlin.reflect.KClass

enum class MeetingUpdateEventName(override val eventClass: KClass<*>) : UpdateEventName {
    CREATE_MEETING_EVENT(PlanMeetingEvent::class),
    COMPLETE_MEETING_EVENT(CompleteMeetingEvent::class),
    UPDATE_MEETING_EVENT(UpdateMeetingEvent::class)
}

data class PlanMeetingEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID = UUID.randomUUID(),
    override val correlationId: String,
    val title: String,
    val date: Long,
    val participantIds: List<String>,
    val questionIds: List<String>,
    val teamId: String,
) : UpdateEvent<MeetingEventSourcingEntity, MeetingUpdateEventName>() {
    override val name: MeetingUpdateEventName = MeetingUpdateEventName.CREATE_MEETING_EVENT

    override infix fun applyTo(entity: MeetingEventSourcingEntity) {
        entity.entityId = entityId.toString()
        entity.projectId = correlationId

        entity.title = title
        entity.date = date
        entity.status = "PLANNED"
        entity.teamId = teamId
        entity.participantIds = participantIds.toMutableList()
        entity.questionIds = questionIds.toMutableList()

        entity.createdAt = createdAt
        entity.updatedAt = createdAt
    }
}

data class CompleteMeetingEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID,
    override val correlationId: String,
) : UpdateEvent<MeetingEventSourcingEntity, MeetingUpdateEventName>() {
    override val name: MeetingUpdateEventName = MeetingUpdateEventName.COMPLETE_MEETING_EVENT

    override infix fun applyTo(entity: MeetingEventSourcingEntity) {
        entity.status = "COMPLETED"
        entity.updatedAt = createdAt
    }
}

data class UpdateMeetingEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID,
    override val correlationId: String,
    val title: String? = null,
    val date: Long? = null,
    val participantIds: List<String>? = null,
    val questionIds: List<String>? = null,
    val teamId: String? = null
) : UpdateEvent<MeetingEventSourcingEntity, MeetingUpdateEventName>() {
    override val name: MeetingUpdateEventName = MeetingUpdateEventName.UPDATE_MEETING_EVENT

    override infix fun applyTo(entity: MeetingEventSourcingEntity) {
        title?.let {
            entity.title = title
        }

        date?.let {
            entity.date = it
        }

        participantIds?.let {
            entity.participantIds = it.toMutableList()
        }

        questionIds?.let {
            entity.questionIds = it.toMutableList()
        }

        teamId?.let {
            entity.teamId = it
        }

        entity.updatedAt = createdAt
    }
}