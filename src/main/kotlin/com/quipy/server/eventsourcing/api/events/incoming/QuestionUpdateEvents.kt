package com.quipy.server.eventsourcing.api.events.incoming

import com.quipy.server.eventsourcing.UpdateEvent
import com.quipy.server.eventsourcing.UpdateEventName
import com.quipy.server.eventsourcing.entities.Comment
import com.quipy.server.eventsourcing.entities.QuestionEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.QuestionUpdateEventName.*
import java.util.*
import kotlin.reflect.KClass

enum class QuestionUpdateEventName(override val eventClass: KClass<*>) : UpdateEventName {
    UPDATE_QUESTION_EVENT(UpdateQuestionEvent::class),
    MARK_COMMENT_AS_ANSWER_EVENT(MarkCommentAsAnswerEvent::class),
    UNMARK_COMMENT_AS_ANSWER_EVENT(UnmarkCommentAsAnswerEvent::class),
    CREATE_QUESTION_EVENT(CreateQuestionEvent::class),
    ASSIGN_USER_QUESTION_EVENT(AssignUserQuestionEvent::class),
    ADD_COMMENT_QUESTION_EVENT(AddCommentQuestionEvent::class);
}

data class UpdateQuestionEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID,
    override var correlationId: String,
    val title: String? = null,
    val description: String? = null,
    val assignedToUserIds: List<String>? = null,
    val isAnonymous: Boolean? = null
) : UpdateEvent<QuestionEventSourcingEntity, QuestionUpdateEventName>() {
    override val name: QuestionUpdateEventName = UPDATE_QUESTION_EVENT

    override infix fun applyTo(entity: QuestionEventSourcingEntity) {
        entity.updatedAt = createdAt

        title?.let {
            entity.title = it
        }

        entity.description = description
        entity.assignedToUserIds = assignedToUserIds
        entity.isAnonymous = isAnonymous
        entity.watchers.addAll(assignedToUserIds ?: emptyList())
    }
}

data class MarkCommentAsAnswerEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID,
    override var correlationId: String,
    val commentId: String
) : UpdateEvent<QuestionEventSourcingEntity, QuestionUpdateEventName>() {
    override val name: QuestionUpdateEventName = MARK_COMMENT_AS_ANSWER_EVENT

    override infix fun applyTo(entity: QuestionEventSourcingEntity) {
        val updatedComments = mutableListOf<Comment>()

        for (comment in entity.comments) {
            if (comment.id == this@MarkCommentAsAnswerEvent.commentId) {
                comment.isAnswer = true
            }

            updatedComments.add(comment)
        }

        entity.isResolved = true
        entity.updatedAt = createdAt
    }
}

data class UnmarkCommentAsAnswerEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID,
    override var correlationId: String,
    val commentId: String
) : UpdateEvent<QuestionEventSourcingEntity, QuestionUpdateEventName>() {
    override val name: QuestionUpdateEventName = UNMARK_COMMENT_AS_ANSWER_EVENT

    override infix fun applyTo(entity: QuestionEventSourcingEntity) {
        val updatedComments = mutableListOf<Comment>()

        for (comment in entity.comments) {
            if (comment.id == this@UnmarkCommentAsAnswerEvent.commentId) {
                comment.isAnswer = false
            }

            updatedComments.add(comment)
        }

        entity.isResolved = entity.comments.any { it.isAnswer }
        entity.updatedAt = createdAt
    }
}

data class CreateQuestionEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID = UUID.randomUUID(),
    override var correlationId: String,
    val title: String,
    val description: String,
    val isAnonymous: Boolean,
    val authorId: UUID,
    val meetingId: UUID? = null,
    val parentQuestionId: UUID? = null,
    val assignedToUserIds: List<String>?
) : UpdateEvent<QuestionEventSourcingEntity, QuestionUpdateEventName>() {
    override val name: QuestionUpdateEventName = CREATE_QUESTION_EVENT

    override infix fun applyTo(entity: QuestionEventSourcingEntity) {
        entity.entityId = entityId.toString()
        entity.projectId = correlationId
        entity.title = title
        entity.description = description
        entity.isAnonymous = isAnonymous
        entity.authorId = authorId
        entity.assignedToUserIds = assignedToUserIds
        entity.meetingId = meetingId
        entity.parentQuestionId = parentQuestionId
        entity.createdAt = createdAt
        entity.updatedAt = createdAt

        entity.watchers.run {
            addAll(assignedToUserIds ?: emptyList())
            add(authorId.toString())
        }
    }
}

data class AssignUserQuestionEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID,
    override var correlationId: String,
    val userIds: List<String>,
) : UpdateEvent<QuestionEventSourcingEntity, QuestionUpdateEventName>() {
    override val name: QuestionUpdateEventName = ASSIGN_USER_QUESTION_EVENT

    override infix fun applyTo(entity: QuestionEventSourcingEntity) {
        entity.assignedToUserIds = userIds
        entity.updatedAt = createdAt
        entity.watchers.addAll(userIds)
    }
}

data class AddCommentQuestionEvent(
    override val id: String = UUID.randomUUID().toString(),
    override val entityId: UUID,
    override var correlationId: String,
    val commentId: String,
    val commentContent: String,
    val authorId: UUID,
    val taskId: UUID? = null
) : UpdateEvent<QuestionEventSourcingEntity, QuestionUpdateEventName>() {
    override val name: QuestionUpdateEventName = ADD_COMMENT_QUESTION_EVENT

    override infix fun applyTo(entity: QuestionEventSourcingEntity) {
        entity.comments.add(
            Comment(
                id = commentId,
                content = commentContent,
                authorId = authorId,
                questionId = entityId,
                createdAt = System.currentTimeMillis(),
                isAnswer = false,
                taskId = taskId
            )
        )
        entity.updatedAt = createdAt
        entity.watchers.add(authorId.toString())
    }
}