package com.quipy.server.query.entities

import com.quipy.server.query.VersionedEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "tasks")
data class Task(
    @Id
    override val id: String,
    val projectId: String,
    val title: String,
    var description: String?,
    var assignedTo: User?,
    val status: String,
    var associatedQuestion: Question?,
    var associatedMeeting: Meeting?,
    override val version: Int
) : VersionedEntity