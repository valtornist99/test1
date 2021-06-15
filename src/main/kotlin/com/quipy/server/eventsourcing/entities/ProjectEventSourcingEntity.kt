package com.quipy.server.eventsourcing.entities

import com.quipy.server.eventsourcing.EventSourcingEntity
import java.util.*

data class ProjectEventSourcingEntity(
    override var entityId: String = UUID.randomUUID().toString(),
) : EventSourcingEntity {
    override var createdAt: Long = 0
    override var updatedAt: Long = 0
    override var version: Int = 0
    lateinit var title: String
    lateinit var memberIds: MutableList<String>


    override fun toString(): String {
        return "ProjectEventSourcingEntity(entityId='$entityId', createdAt=$createdAt, updatedAt=$updatedAt, version=$version, title='$title')"
    }

}