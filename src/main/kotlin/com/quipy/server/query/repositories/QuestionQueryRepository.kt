package com.quipy.server.query.repositories

import com.quipy.server.query.entities.Question
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuestionQueryRepository : MongoRepository<Question, String> {
    fun findAllByProjectId(projectId: String): List<Question>
    fun findByProjectIdAndId(projectId: String, id: String): Question
    fun findByAssignedToId(userId: String): List<Question>
    fun findByIdAndVersion(id: String, version: Int): Optional<Question>
    fun findAllByWatchersIdIn(userId: String): List<Question> // todo sukhoa check if it works at all
}