package com.quipy.server.eventsourcing.api.events.incoming

import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.UpdateEventName
import com.quipy.server.eventsourcing.entities.TeamEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.TeamUpdateEventName.ADD_TEAM_MEMBER_EVENT
import com.quipy.server.eventsourcing.api.events.incoming.TeamUpdateEventName.CREATE_TEAM_EVENT
import java.util.*
import kotlin.reflect.KClass

enum class TeamUpdateEventName(override val eventClass: KClass<*>) : UpdateEventName {
    CREATE_TEAM_EVENT(CreateTeamEvent::class),
    ADD_TEAM_MEMBER_EVENT(AddTeamMemberEvent::class)
}

data class CreateTeamEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID = UUID.randomUUID(),
    override val correlationId: String?,
    val title: String,
) : UpdateEvent<TeamEventSourcingEntity, TeamUpdateEventName>() {
    override val name: TeamUpdateEventName = CREATE_TEAM_EVENT

    override infix fun applyTo(entity: TeamEventSourcingEntity) {
        entity.entityId = entityId.toString()
        entity.title = title
        entity.memberIds = mutableListOf()
    }
}

data class AddTeamMemberEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID = UUID.randomUUID(),
    override val correlationId: String?,
    val userId: String,
) : UpdateEvent<TeamEventSourcingEntity, TeamUpdateEventName>() {
    override val name: TeamUpdateEventName = ADD_TEAM_MEMBER_EVENT

    override infix fun applyTo(entity: TeamEventSourcingEntity) {
        entity.memberIds.add(userId)
    }
}