package com.quipy.server.eventsourcing.entities

import com.quipy.server.eventsourcing.EventSourcingEntity
import java.util.*

data class TeamEventSourcingEntity(
    override var entityId: String = UUID.randomUUID().toString(),
) : EventSourcingEntity {
    lateinit var projectId: String
    override var createdAt: Long = 0
    override var updatedAt: Long = 0
    override var version: Int = 0
    lateinit var title: String
    lateinit var memberIds: MutableList<String>

    override fun toString(): String {
        return "TeamEventSourcingEntity(entityId='$entityId', projectId='$projectId', createdAt=$createdAt, updatedAt=$updatedAt, version=$version, title='$title', assignedUserIds=$memberIds)"
    }
}