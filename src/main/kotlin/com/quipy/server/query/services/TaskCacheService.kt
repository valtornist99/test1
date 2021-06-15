package com.quipy.server.query.services

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.quipy.server.query.entities.User
import com.quipy.server.eventsourcing.entities.TaskEventSourcingEntity
import com.quipy.server.query.entities.Task
import com.quipy.server.query.repositories.MeetingQueryRepository
import com.quipy.server.query.repositories.QuestionQueryRepository
import com.quipy.server.query.updateWithLatestVersion
import com.quipy.server.query.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class TaskCacheService {
    @Autowired
    lateinit var cacheUpdateEventBus: EventBus

    @Autowired
    lateinit var questionQueryRepository: QuestionQueryRepository

    @Autowired
    lateinit var meetingQueryRepository: MeetingQueryRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @PostConstruct
    fun init() {
        cacheUpdateEventBus.register(this)
    }

    @Subscribe
    fun updateProjectCache(updatedTask: TaskEventSourcingEntity) {

        val task = Task(
            id = updatedTask.entityId,
            title = updatedTask.title,
            description = updatedTask.description,
            projectId = updatedTask.projectId,
            status = updatedTask.status,
            version = updatedTask.version,
            assignedTo = null,
            associatedQuestion = null,
            associatedMeeting = null
        )

        if (!updatedTask.assignedToUserId.isNullOrEmpty()) {
            val assignedToUser = userRepository.findById(updatedTask.assignedToUserId.toString()).get()
            task.assignedTo = User(
                assignedToUser.id,
                assignedToUser.name,
                assignedToUser.uid
            )
        }

        if (!updatedTask.associatedQuestionId.isNullOrEmpty()) {
            val question = questionQueryRepository.findById(updatedTask.associatedQuestionId!!).get()
            task.associatedQuestion = question
        }

        if (!updatedTask.associatedMeetingId.isNullOrEmpty()) {
            val meeting = meetingQueryRepository.findById(updatedTask.associatedMeetingId!!).get()
            task.associatedMeeting = meeting
        }

        mongoTemplate.updateWithLatestVersion(task)
    }
}