package com.quipy.server.query.repositories

import com.quipy.server.query.entities.Meeting
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface MeetingQueryRepository : MongoRepository<Meeting, String> {
    fun findAllByProjectId(projectId: String): List<Meeting>
    fun findByParticipantsId(participantId: String): List<Meeting>
}