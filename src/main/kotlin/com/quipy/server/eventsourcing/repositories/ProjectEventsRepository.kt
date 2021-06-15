package com.quipy.server.eventsourcing.repositories

import com.quipy.server.eventsourcing.records.ProjectEventSourcingRecord
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ProjectEventsRepository : MongoRepository<ProjectEventSourcingRecord, UUID> {
    fun findAllByEntityId(entityId: String): List<ProjectEventSourcingRecord>
}