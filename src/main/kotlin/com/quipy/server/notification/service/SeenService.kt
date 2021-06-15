package com.quipy.server.notification.service

import com.quipy.server.notification.dto.SeenDto
import com.quipy.server.notification.entity.SeenInfo
import com.quipy.server.notification.repository.SeenRepository
import com.quipy.server.notification.repository.ThreadsRepository
import com.quipy.server.query.updateWithLatestVersion
import com.quipy.server.query.repositories.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class SeenService(
    val userRepository: UserRepository,
    var threadsRepository: ThreadsRepository,
    var mongoTemplate: MongoTemplate,
    var seenRepository: SeenRepository
) {
    companion object {
        val logger = LoggerFactory.getLogger(SeenService::class.java)
    }

    fun threadSeenByUser(questionId: String, userId: String, seenVersion: Int? = null) {
        val threadOpt = threadsRepository.findById(questionId)
        when {
            threadOpt.isEmpty -> {
                logger.warn("There is no such thread to mark seen: $questionId")
                return
            }
            else -> {
                val thread = threadOpt.get()
                if (!thread.watchers.contains(userId)) {
                    logger.error("User $userId is not watching the question: $questionId")
                    return
                }

                val seenId = seenRepository.findByUserIdAndQuestionId(userId, questionId) // todo sukhoa race condition. may end up with multiple records for unique pair (userId, questionId)
                    .map { it.id }
                    .orElse(UUID.randomUUID().toString())

                val seenInfo = SeenInfo(
                    id = seenId,
                    questionId = questionId,
                    version = seenVersion ?: thread.version,
                    userId = userId,
                    numberOfSeenComments = thread.numberOfComments, // todo sukhoa version from listener, number from thread might be inconsistent
                    projectId = thread.projectId
                )

                mongoTemplate.updateWithLatestVersion(seenInfo)
            }
        }
    }

    fun getSeensByProject(userId: String, projectId: String): List<SeenDto> {
        val seenByThread = seenRepository.findAllByUserIdAndProjectId(userId, projectId).associateBy { it.questionId }

        if (seenByThread.isNullOrEmpty()) {
            return emptyList()
        }

        return threadsRepository.findAllByIdIn(seenByThread.keys)
            .mapNotNull { thread ->
                seenByThread[thread.id]?.run {
                    SeenDto(
                        questionId = id,
                        numberOfUnreadComments = thread.numberOfComments - numberOfSeenComments
                    )
                }
            }.toList()
    }

    fun getSeensForQuestions(userId: String, questionIds: Set<String>): List<SeenDto> {
        val seenByThread = seenRepository.findAllByUserIdAndQuestionIdIn(userId, questionIds).associateBy { it.questionId }

        if (seenByThread.isNullOrEmpty()) {
            return emptyList()
        }

        return threadsRepository.findAllByIdIn(questionIds)
            .mapNotNull { thread ->
                seenByThread[thread.id]?.run {
                    SeenDto(
                        questionId = id,
                        numberOfUnreadComments = thread.numberOfComments - numberOfSeenComments
                    )
                }
            }.toList()
    }
}