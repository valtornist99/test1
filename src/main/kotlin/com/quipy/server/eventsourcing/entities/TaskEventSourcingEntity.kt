package com.quipy.server.eventsourcing.entities

import com.quipy.server.eventsourcing.EventSourcingEntity
import java.util.*

data class TaskEventSourcingEntity(
    override var entityId: String = UUID.randomUUID().toString(),
) : EventSourcingEntity {
    lateinit var projectId: String
    override var createdAt: Long = 0
    override var updatedAt: Long = 0
    override var version: Int = 0
    lateinit var title: String
    var description: String? = ""
    var assignedToUserId: String? = ""
    var associatedQuestionId: String? = ""
    var associatedMeetingId: String? = ""
    lateinit var status: String

    override fun toString(): String {
        return "TaskEventSourcingEntity(entityId='$entityId', createdAt=$createdAt, updatedAt=$updatedAt, version=$version, title='$title', assignedTo=$assignedToUserId, status='$status')"
    }
}