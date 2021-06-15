package com.quipy.server.query.repositories

import com.quipy.server.query.entities.Project
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectQueryRepository : MongoRepository<Project, String> {
    fun findByIdAndVersion(id: String, version: Int): Optional<Project>
    fun findByMembersContaining(userId: String): List<Project>
    fun findByMembersId(userId: String): List<Project>
}