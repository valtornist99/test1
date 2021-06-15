package com.quipy.server.query.entities

import com.quipy.server.query.VersionedEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "meetings")
data class Meeting(
    @Id
    override val id: String,
    val projectId: String,
    val title: String,
    val date: Long?,
    val team: Team,
    val status: String,
    val questions: List<Question>,
    val participants: List<User>,
    override val version: Int
) : VersionedEntity