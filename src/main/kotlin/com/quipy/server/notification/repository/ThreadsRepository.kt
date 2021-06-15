package com.quipy.server.notification.repository

import com.quipy.server.notification.entity.ThreadInfo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ThreadsRepository : MongoRepository<ThreadInfo, String> {
    fun findAllByIdIn(ids: Set<String>) : List<ThreadInfo>
}