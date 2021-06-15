package com.quipy.server.notification.repository

import com.quipy.server.notification.entity.SeenInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SeenRepository : MongoRepository<SeenInfo, String> {
    fun findByUserIdAndQuestionId(userId: String, questionId: String): Optional<SeenInfo>
    fun findAllByUserIdAndProjectId(userId: String, projectId: String): List<SeenInfo>
    fun findAllByUserIdAndQuestionIdIn(userId: String, questionIds: Set<String>): List<SeenInfo>
}