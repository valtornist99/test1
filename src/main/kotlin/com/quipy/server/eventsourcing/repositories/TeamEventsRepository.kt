package com.quipy.server.eventsourcing.repositories

import com.quipy.server.eventsourcing.records.TeamEventSourcingRecord
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TeamEventsRepository : MongoRepository<TeamEventSourcingRecord, UUID> {
    fun findAllByEntityId(entityId: String): List<TeamEventSourcingRecord>
}