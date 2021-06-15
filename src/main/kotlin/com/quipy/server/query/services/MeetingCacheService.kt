package com.quipy.server.query.services

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.quipy.server.eventsourcing.entities.MeetingEventSourcingEntity
import com.quipy.server.query.entities.Meeting
import com.quipy.server.query.repositories.QuestionQueryRepository
import com.quipy.server.query.repositories.TeamQueryRepository
import com.quipy.server.query.updateWithLatestVersion
import com.quipy.server.query.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class MeetingCacheService {
    @Autowired
    lateinit var cacheUpdateEventBus: EventBus

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var questionQueryRepository: QuestionQueryRepository

    @Autowired
    lateinit var teamQueryRepository: TeamQueryRepository

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @PostConstruct
    fun init() {
        cacheUpdateEventBus.register(this)
    }

    @Subscribe
    fun updateMeetingCache(updatedMeeting: MeetingEventSourcingEntity) {
        val participants = updatedMeeting.participantIds.mapNotNull { userRepository.findById(it).get() }
        val questions = updatedMeeting.questionIds.mapNotNull { questionQueryRepository.findById(it).get() }
        val team = teamQueryRepository.findById(updatedMeeting.teamId).get()

        val task = Meeting(
            id = updatedMeeting.entityId,
            title = updatedMeeting.title,
            projectId = updatedMeeting.projectId,
            date = updatedMeeting.date,
            questions = questions,
            participants = participants,
            team = team,
            status = updatedMeeting.status,
            version = updatedMeeting.version,
        )

        mongoTemplate.updateWithLatestVersion(task)
    }
}