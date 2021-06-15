package com.quipy.server.query.repositories

import com.quipy.server.query.entities.User
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface UserRepository : MongoRepository<User, String> {
    override fun findById(id: String): Optional<User>
    fun findByUid(uid: String): Optional<User>
    fun existsByUid(uid: String): Boolean
}