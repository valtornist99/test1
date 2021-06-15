package com.quipy.server.eventsourcing.services

import com.quipy.server.query.entities.User
import com.quipy.server.eventsourcing.api.events.incoming.*
import com.quipy.server.eventsourcing.entities.ProjectEventSourcingEntity
import com.quipy.server.eventsourcing.entities.QuestionEventSourcingEntity
import com.quipy.server.eventsourcing.entities.TeamEventSourcingEntity
import com.quipy.server.eventsourcing.repositories.MeetingEventsRepository
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
internal class EventSourcingMeetingServiceTest() {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var eventSourcingTeamService: EventSourcingTeamService

    @Autowired
    lateinit var eventSourcingProjectService: EventSourcingProjectService

    @Autowired
    lateinit var eventSourcingQuestionService: EventSourcingQuestionService

    @Autowired
    lateinit var eventSourcingMeetingService: EventSourcingMeetingService

    @Autowired
    lateinit var meetingEventsRepository: MeetingEventsRepository

    lateinit var defaultUser: User
    lateinit var defaultTeam: TeamEventSourcingEntity
    lateinit var defaultProject: ProjectEventSourcingEntity
    lateinit var defaultQuestion: QuestionEventSourcingEntity

    lateinit var defaultPlanMeetingEvent: PlanMeetingEvent

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

        // create default question
        val createQuestionEvent = CreateQuestionEvent(
            correlationId = defaultProject.entityId,
            title = "Default Question",
            description = "Default Description",
            isAnonymous = true,
            authorId = UUID.fromString(defaultUser.id),
            assignedToUserIds = null
        )
        eventSourcingQuestionService.update(createQuestionEvent)
        defaultQuestion = eventSourcingQuestionService.get(createQuestionEvent.entityId)

        defaultPlanMeetingEvent = PlanMeetingEvent(
            correlationId = defaultProject.entityId,
            title = "Test Meeting",
            date = 0,
            participantIds = listOf(defaultUser.id),
            questionIds = listOf(defaultQuestion.entityId),
            teamId = defaultTeam.entityId
        )
    }

    @AfterEach()
    fun cleanUp() {
        meetingEventsRepository.deleteAll()
    }

    @Test
    fun `creates meeting and saves it`() {
        eventSourcingMeetingService.update(defaultPlanMeetingEvent)
        val meetingEntity = eventSourcingMeetingService.get(defaultPlanMeetingEvent.entityId)

        assert(meetingEntity.entityId == defaultPlanMeetingEvent.entityId.toString())
        assert(meetingEntity.projectId == defaultPlanMeetingEvent.correlationId)
        assert(meetingEntity.title == defaultPlanMeetingEvent.title)
        assert(meetingEntity.date == defaultPlanMeetingEvent.date)
        assert(meetingEntity.participantIds.contains(defaultUser.id))
        assert(meetingEntity.questionIds.contains(defaultQuestion.entityId))
        assert(meetingEntity.teamId == defaultPlanMeetingEvent.teamId)
    }

    @Test
    fun `edit values of meeting`() {
        eventSourcingMeetingService.update(defaultPlanMeetingEvent)
        var meetingEntity = eventSourcingMeetingService.get(defaultPlanMeetingEvent.entityId)

        val editMeetingEvent = UpdateMeetingEvent(
            entityId = UUID.fromString(meetingEntity.entityId),
            correlationId = meetingEntity.projectId,
            title = "Changed Title",
            date = 1000,
            participantIds = listOf(),
            questionIds = listOf(),
            teamId = null
        )
        eventSourcingMeetingService.update(editMeetingEvent)
        meetingEntity = eventSourcingMeetingService.get(defaultPlanMeetingEvent.entityId)

        assert(meetingEntity.entityId == editMeetingEvent.entityId.toString())
        assert(meetingEntity.projectId == editMeetingEvent.correlationId)
        assert(meetingEntity.title == editMeetingEvent.title)
        assert(meetingEntity.date == editMeetingEvent.date)
        assert(meetingEntity.participantIds.isEmpty())
        assert(meetingEntity.questionIds.isEmpty())
    }

    @Test
    fun `change status of meeting`() {
        eventSourcingMeetingService.update(defaultPlanMeetingEvent)
        var meetingEntity = eventSourcingMeetingService.get(defaultPlanMeetingEvent.entityId)

        assert(meetingEntity.status == "PLANNED")

        val completeMeetingEvent = CompleteMeetingEvent(
            entityId = UUID.fromString(meetingEntity.entityId),
            correlationId = meetingEntity.projectId
        )

        eventSourcingMeetingService.update(completeMeetingEvent)
        meetingEntity = eventSourcingMeetingService.get(defaultPlanMeetingEvent.entityId)

        assert(meetingEntity.status == "COMPLETED")
    }
}