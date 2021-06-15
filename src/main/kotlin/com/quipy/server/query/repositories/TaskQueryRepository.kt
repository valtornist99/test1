package com.quipy.server.query.repositories

import com.quipy.server.query.entities.Task
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskQueryRepository : MongoRepository<Task, String> {
    fun findAllByProjectId(projectId: String): List<Task>
    fun findByProjectIdAndId(projectId: String, id: String): Task
    fun findByAssignedToId(userId: String): List<Task>
}