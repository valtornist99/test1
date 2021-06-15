package com.quipy.server.eventsourcing.repositories

import com.quipy.server.eventsourcing.records.MeetingEventSourcingRecord
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MeetingEventsRepository : MongoRepository<MeetingEventSourcingRecord, UUID> {
    fun findAllByEntityId(entityId: String): List<MeetingEventSourcingRecord>
}