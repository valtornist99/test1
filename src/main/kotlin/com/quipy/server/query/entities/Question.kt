package com.quipy.server.query.entities

import com.quipy.server.query.VersionedEntity
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


data class Comment(val id: String, val author: User, val content: String, val isAnswer: Boolean)

@Document(collection = "questions")
data class Question(
    @Id
    override val id: String,
    override val version: Int,

    val projectId: String,

    val title: String,
    val description: String?,

    val isResolved: Boolean,
    val isAnonymous: Boolean?,

    val author: User,
    val meeting: Meeting?,
    val comments: List<Comment>,
    val assignedTo: List<User>?,
    val watchers: List<User>?,


    val createdAt: Long,
    val updatedAt: Long,

    var numberOfUnreadComments: Int = 0,
) : VersionedEntity
