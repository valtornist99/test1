package com.quipy.server.notification.entity

import com.quipy.server.query.VersionedEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "seen_info")
class SeenInfo(
    @Id
    override val id: String, // synthetic Id. The real id is pair (questionId, userId)
    override val version: Int, // the latest version of the question that was seen by user
    val questionId: String, // here is the Id of the thread (the same as questionId)
    val userId: String,
    val numberOfSeenComments: Int,
    val projectId: String
) : VersionedEntity