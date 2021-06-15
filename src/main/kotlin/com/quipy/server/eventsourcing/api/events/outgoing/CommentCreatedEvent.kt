package com.quipy.server.eventsourcing.api.events.outgoing

import java.util.*

data class CommentCreatedEvent(
    val id: String = UUID.randomUUID().toString(),
    val projectId: String,
    val questionId: String,
    val commentId: String,
    val totalNumberOfComments: Int,
    val questionVersion: Int,
    val questionWatchers: MutableSet<String> = mutableSetOf(),
    val authorId: String
)