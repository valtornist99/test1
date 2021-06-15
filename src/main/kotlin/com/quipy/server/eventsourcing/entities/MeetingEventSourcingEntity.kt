package com.quipy.server.eventsourcing.entities

import com.quipy.server.eventsourcing.EventSourcingEntity
import java.util.*

data class MeetingEventSourcingEntity(
    override var entityId: String = UUID.randomUUID().toString(),
) : EventSourcingEntity {
    lateinit var projectId: String
    override var createdAt: Long = 0
    override var updatedAt: Long = 0
    override var version: Int = 0
    lateinit var title: String
    var date: Long? = null
    lateinit var participantIds: MutableList<String>
    lateinit var questionIds: MutableList<String>
    lateinit var teamId: String
    lateinit var status: String
}