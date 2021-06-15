package com.quipy.server.eventsourcing.api.events.incoming

import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.UpdateEventName
import com.quipy.server.eventsourcing.entities.ProjectEventSourcingEntity
import java.util.*
import kotlin.reflect.KClass

enum class ProjectUpdateEventName(override val eventClass: KClass<*>) : UpdateEventName {
    CREATE_PROJECT_EVENT(CreateProjectEvent::class),
    EDIT_PROJECT_TITLE_EVENT(EditProjectTitleEvent::class),
    ADD_PROJECT_MEMBER_EVENT(AddProjectMemberEvent::class),
}

data class CreateProjectEvent(
    override val id: String = UUID.randomUUID().toString(),
    override var entityId: UUID,
    override val correlationId: String? = null,
    val creatorId: String?,
    val title: String
) : UpdateEvent<ProjectEventSourcingEntity, ProjectUpdateEventName>() {
    override val name: ProjectUpdateEventName = ProjectUpdateEventName.CREATE_PROJECT_EVENT

    override infix fun applyTo(entity: ProjectEventSourcingEntity) {
        val memberIds = mutableListOf<String>()
        creatorId?.let {
            memberIds.add(it)
        }

        entity.entityId = entityId.toString()
        entity.title = title
        entity.memberIds = memberIds
        entity.createdAt = createdAt
        entity.updatedAt = createdAt
    }
}

data class EditProjectTitleEvent(
    override val id: String = UUID.randomUUID().toString(),
    override var entityId: UUID,
    override val correlationId: String? = null,
    val title: String
) : UpdateEvent<ProjectEventSourcingEntity, ProjectUpdateEventName>() {
    override val name: ProjectUpdateEventName = ProjectUpdateEventName.EDIT_PROJECT_TITLE_EVENT

    override infix fun applyTo(entity: ProjectEventSourcingEntity) {
        entity.title = title
        entity.updatedAt = createdAt
    }
}

data class AddProjectMemberEvent(
    override val id: String = UUID.randomUUID().toString(),
    override var entityId: UUID,
    override val correlationId: String? = null,
    val userId: String
) : UpdateEvent<ProjectEventSourcingEntity, ProjectUpdateEventName>() {
    override val name: ProjectUpdateEventName = ProjectUpdateEventName.ADD_PROJECT_MEMBER_EVENT

    override infix fun applyTo(entity: ProjectEventSourcingEntity) {
        entity.memberIds.add(userId)
        entity.updatedAt = createdAt
    }
}