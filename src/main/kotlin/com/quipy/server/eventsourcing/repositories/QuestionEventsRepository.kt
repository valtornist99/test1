package com.quipy.server.eventsourcing.repositories

import com.quipy.server.eventsourcing.records.QuestionEventSourcingRecord
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface QuestionEventsRepository : MongoRepository<QuestionEventSourcingRecord, UUID> {
    fun findAllByEntityId(entityId: String): List<QuestionEventSourcingRecord>
}