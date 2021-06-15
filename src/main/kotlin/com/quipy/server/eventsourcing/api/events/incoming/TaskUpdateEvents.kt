package com.quipy.server.eventsourcing.api.events.incoming

import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.UpdateEventName
import com.quipy.server.eventsourcing.entities.TaskEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.TaskUpdateEventName.*
import java.util.*
import kotlin.reflect.KClass

enum class TaskUpdateEventName(override val eventClass: KClass<*>) : UpdateEventName {
    CREATE_TASK_EVENT(CreateTaskEvent::class),
    UPDATE_TASK_EVENT(UpdateTaskEvent::class),
    CHANGE_TASK_STATUS_EVENT(ChangeTaskStatusEvent::class)
}

data class CreateTaskEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID = UUID.randomUUID(),
    override val correlationId: String,
    val title: String,
    val description: String?,
    val assignedToUserId: String?,
    var associatedMeetingId: String? = null,
    var associatedQuestionId: String? = null,
) : UpdateEvent<TaskEventSourcingEntity, TaskUpdateEventName>() {
    override val name: TaskUpdateEventName = CREATE_TASK_EVENT

    override infix fun applyTo(entity: TaskEventSourcingEntity) {
        entity.entityId = entityId.toString()
        entity.projectId = correlationId
        entity.title = title
        entity.description = description
        entity.status = "BACKLOG"
        entity.assignedToUserId = assignedToUserId
        entity.associatedQuestionId = associatedQuestionId
        entity.associatedMeetingId = associatedMeetingId
        entity.createdAt = createdAt
        entity.updatedAt = createdAt
    }
}

data class UpdateTaskEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID = UUID.randomUUID(),
    override val correlationId: String,
    val title: String? = null,
    val description: String? = null,
    val assignedToUserId: String? = null,
    var associatedMeetingId: String? = null,
    var associatedQuestionId: String? = null,
) : UpdateEvent<TaskEventSourcingEntity, TaskUpdateEventName>() {
    override val name: TaskUpdateEventName = UPDATE_TASK_EVENT

    override infix fun applyTo(entity: TaskEventSourcingEntity) {
        title?.let {
            entity.title = it
        }

        entity.assignedToUserId = assignedToUserId
        entity.description = description
        entity.associatedQuestionId = associatedQuestionId
        entity.associatedMeetingId = associatedMeetingId

        entity.updatedAt = createdAt
    }
}

data class ChangeTaskStatusEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val correlationId: String? = null,
    override val entityId: UUID,
    val status: String,
) : UpdateEvent<TaskEventSourcingEntity, TaskUpdateEventName>() {
    override val name: TaskUpdateEventName = CHANGE_TASK_STATUS_EVENT

    override infix fun applyTo(entity: TaskEventSourcingEntity) {
        entity.status = status
        entity.updatedAt = createdAt
    }
}