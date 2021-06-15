package com.quipy.server.eventsourcing.entities

import com.quipy.server.eventsourcing.EventSourcingEntity
import java.util.*

data class Comment(
    val id: String,
    val content: String,
    val authorId: UUID,
    val questionId: UUID,
    val createdAt: Long,
    var isAnswer: Boolean = false,
    var taskId: UUID? = null,
) {
    override fun toString(): String {
        return "Comment(" +
            "id='$id'," +
            "content='$content'," +
            "authorId=$authorId," +
            "questionId=$questionId," +
            "createdAt=$createdAt," +
            "isAnswer=$isAnswer" +
            "taskId=$taskId" +
            ")"
    }
}

class QuestionEventSourcingEntity(
    override var entityId: String
) : EventSourcingEntity {
    override var version: Int = 0
    override var createdAt: Long = 0 // todo think of something to not to have this fields duplicated everywhere
    override var updatedAt: Long = 0

    lateinit var title: String
    var description: String? = ""
    lateinit var projectId: String
    var isAnonymous: Boolean? = true
    var isResolved: Boolean = false
    lateinit var authorId: UUID
    var meetingId: UUID? = null
    var parentQuestionId: UUID? = null
    var comments: MutableList<Comment> = mutableListOf()
    var watchers: MutableSet<String> = mutableSetOf()
    var assignedToUserIds: List<String>? = listOf()

    override fun toString(): String {
        return "Question(\n" +
            "   entityId='$entityId',\n" +
            "   title='$title',\n" +
            "   description='$description',\n" +
            "   isAnonymous=$isAnonymous,\n" +
            "   authorId=$authorId,\n" +
            "   meetingId=$meetingId,\n" +
            "   parentQuestionId=$parentQuestionId,\n" +
            "   assignedTo=$assignedToUserIds,\n" +
            "   comments=$comments)"
    }
}