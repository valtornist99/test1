package com.quipy.server.query.services

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.quipy.server.query.entities.User
import com.quipy.server.eventsourcing.entities.QuestionEventSourcingEntity
import com.quipy.server.query.entities.Comment
import com.quipy.server.query.entities.Question
import com.quipy.server.query.repositories.QuestionQueryRepository
import com.quipy.server.query.updateWithLatestVersion
import com.quipy.server.query.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class QuestionCacheService {
    @Autowired
    lateinit var cacheUpdateEventBus: EventBus

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var questionQueryRepository: QuestionQueryRepository

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @PostConstruct
    fun init() {
        cacheUpdateEventBus.register(this)
    }

    @Subscribe
    fun updateQuestionCache(updatedQuestion: QuestionEventSourcingEntity) {
        val existingQuestion: Question? =
            questionQueryRepository.findById(updatedQuestion.entityId).orElse(null) // todo make an extension

        val author = userRepository.findById(updatedQuestion.authorId.toString()).get()

        val question = Question(
            id = updatedQuestion.entityId,
            projectId = updatedQuestion.projectId,
            title = updatedQuestion.title,
            description = updatedQuestion.description,
            isResolved = updatedQuestion.isResolved,
            isAnonymous = updatedQuestion.isAnonymous,
            author = User(author.id, author.name, author.uid),
            meeting = null, // fixme
            comments = updatedQuestion.comments.map {
                Comment(
                    id = it.id,
                    author = userRepository.findById(
                        it.authorId.toString()
                    ).get(),
                    content = it.content,
                    isAnswer = it.isAnswer
                )
            },
            assignedTo = updatedQuestion.assignedToUserIds?.mapNotNull { userRepository.findById(it).get() },
            watchers = updatedQuestion.watchers.mapNotNull { userRepository.findById(it).get() },
            createdAt = updatedQuestion.createdAt,
            updatedAt = updatedQuestion.updatedAt,
            version = updatedQuestion.version
        )

        mongoTemplate.updateWithLatestVersion(question)
    }
}