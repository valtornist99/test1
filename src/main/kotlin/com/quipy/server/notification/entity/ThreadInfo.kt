package com.quipy.server.notification.entity

import com.quipy.server.query.VersionedEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "threads")
class ThreadInfo(
    @Id
    override val id: String,
    val lastThreadCommentId: String?,
    val projectId: String,
    val numberOfComments: Int,
    override val version: Int,
    val watchers: MutableSet<String> = mutableSetOf()
) : VersionedEntity
