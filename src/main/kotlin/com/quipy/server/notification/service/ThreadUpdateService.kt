package com.quipy.server.notification.service

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.quipy.server.eventsourcing.api.events.outgoing.CommentCreatedEvent
import com.quipy.server.notification.entity.ThreadInfo
import com.quipy.server.query.updateWithLatestVersion
import com.quipy.server.query.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct


@Service
class ThreadUpdateService {
    companion object {
        val logger = LoggerFactory.getLogger(ThreadUpdateService::class.java)
    }

    @Autowired
    lateinit var domainEventsEventBus: EventBus

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var seenService: SeenService

    @PostConstruct
    fun init() {
        domainEventsEventBus.register(this)
    }

    @Subscribe
    fun updateThread(event: CommentCreatedEvent) {
        logger.info("Updating thread info: ${event.questionId}")

        val threadInfo = ThreadInfo(
            id = event.questionId,
            lastThreadCommentId = event.commentId,
            numberOfComments = event.totalNumberOfComments,
            version = event.questionVersion,
            watchers = event.questionWatchers,
            projectId = event.projectId
        )

        mongoTemplate.updateWithLatestVersion(threadInfo)?.also {
            logger.info("Updated thread: ${it.id} with new version: ${it.version} ")
        }

        seenService.threadSeenByUser(
            questionId = event.questionId,
            userId = event.authorId,
            seenVersion = event.questionVersion
        )
    }
}