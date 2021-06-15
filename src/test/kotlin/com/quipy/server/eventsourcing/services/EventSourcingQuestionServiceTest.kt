package com.quipy.server.eventsourcing.services

import com.quipy.server.query.entities.User
import com.quipy.server.eventsourcing.entities.ProjectEventSourcingEntity
import com.quipy.server.eventsourcing.entities.TeamEventSourcingEntity
import com.quipy.server.eventsourcing.api.events.incoming.CreateProjectEvent
import com.quipy.server.eventsourcing.api.events.incoming.CreateQuestionEvent
import com.quipy.server.eventsourcing.api.events.incoming.CreateTeamEvent
import com.quipy.server.eventsourcing.api.events.incoming.UpdateQuestionEvent
import com.quipy.server.eventsourcing.repositories.QuestionEventsRepository
import com.quipy.server.query.repositories.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EventSourcingQuestionServiceTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var eventSourcingTeamService: EventSourcingTeamService

    @Autowired
    lateinit var eventSourcingProjectService: EventSourcingProjectService

    @Autowired
    lateinit var eventSourcingQuestionService: EventSourcingQuestionService

    @Autowired
    lateinit var questionEventsRepository: QuestionEventsRepository

    lateinit var defaultUser: User
    lateinit var defaultTeam: TeamEventSourcingEntity
    lateinit var defaultProject: ProjectEventSourcingEntity

    lateinit var defaultAskQuestionEvent: CreateQuestionEvent

    @BeforeAll()
    fun setup() {
        // create default user
        val user = User(name = "John", uid = "123456789")
        userRepository.save(user)
        defaultUser = userRepository.findById(user.id).get()

        // create default team
        val createTeamEvent = CreateTeamEvent(correlationId = "", title = "Default Team")
        eventSourcingTeamService.update(createTeamEvent)
        defaultTeam = eventSourcingTeamService.get(createTeamEvent.entityId)

        // create default project
        val createProjectEvent =
            CreateProjectEvent(entityId = UUID.randomUUID(), title = "Test Project", creatorId = user.id)
        eventSourcingProjectService.update(createProjectEvent)
        defaultProject = eventSourcingProjectService.get(createProjectEvent.entityId)

        defaultAskQuestionEvent = CreateQuestionEvent(
            correlationId = defaultProject.entityId,
            title = "Default Question",
            description = "Default Description",
            isAnonymous = true,
            authorId = UUID.fromString(defaultUser.id),
            assignedToUserIds = null
        )
    }

    @AfterEach()
    fun cleanUp() {
        questionEventsRepository.deleteAll()
    }

    @Test
    fun `creates and saves question`() {
        eventSourcingQuestionService.update(defaultAskQuestionEvent)
        val question = eventSourcingQuestionService.get(defaultAskQuestionEvent.entityId)

        assert(question.entityId == defaultAskQuestionEvent.entityId.toString())
        assert(question.title == defaultAskQuestionEvent.title)
        assert(question.description == defaultAskQuestionEvent.description)
        assert(question.assignedToUserIds == defaultAskQuestionEvent.assignedToUserIds)
        assert(question.isAnonymous == defaultAskQuestionEvent.isAnonymous)
        assert(!question.isResolved)
        assert(question.comments.isEmpty())
    }

    @Test
    fun `edits question`() {
        eventSourcingQuestionService.update(defaultAskQuestionEvent)
        var question = eventSourcingQuestionService.get(defaultAskQuestionEvent.entityId)

        val editQuestionEvent = UpdateQuestionEvent(
            entityId = UUID.fromString(question.entityId),
            correlationId = question.projectId,
            title = "Changed Title",
            description = "Changed Description",
            assignedToUserIds = listOf(defaultUser.id),
            isAnonymous = false
        )
        eventSourcingQuestionService.update(editQuestionEvent)
        question = eventSourcingQuestionService.get(defaultAskQuestionEvent.entityId)

        assert(question.entityId == editQuestionEvent.entityId.toString())
        assert(question.title == editQuestionEvent.title)
        assert(question.description == editQuestionEvent.description)
        assert(question.assignedToUserIds == editQuestionEvent.assignedToUserIds)
        assert(question.isAnonymous == editQuestionEvent.isAnonymous)
        assert(!question.isResolved)
        assert(question.comments.isEmpty())
    }
}