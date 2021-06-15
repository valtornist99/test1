package com.quipy.server.eventsourcing.repositories

import com.quipy.server.eventsourcing.records.TaskEventSourcingRecord
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TaskEventsRepository : MongoRepository<TaskEventSourcingRecord, UUID> {
    fun findAllByEntityId(entityId: String): List<TaskEventSourcingRecord>
}