package com.quipy.server.query.repositories

import com.quipy.server.query.entities.Team
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TeamQueryRepository : MongoRepository<Team, String> {
    fun findByIdAndVersion(id: String, version: Int): Optional<Team>
}