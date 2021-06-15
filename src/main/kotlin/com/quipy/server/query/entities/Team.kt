package com.quipy.server.query.entities

import com.quipy.server.query.VersionedEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "teams")
data class Team(
    @Id
    override val id: String,
    val title: String,
    val members: List<User>,
    override val version: Int
) : VersionedEntity