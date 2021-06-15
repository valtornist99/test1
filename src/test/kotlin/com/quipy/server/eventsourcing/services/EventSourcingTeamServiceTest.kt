package com.quipy.server.eventsourcing.services

import com.quipy.server.query.entities.User
import com.quipy.server.eventsourcing.api.events.incoming.AddTeamMemberEvent
import com.quipy.server.eventsourcing.api.events.incoming.CreateTeamEvent
import com.quipy.server.eventsourcing.repositories.TeamEventsRepository
import com.quipy.server.query.repositories.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EventSourcingTeamServiceTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var eventSourcingTeamService: EventSourcingTeamService

    @Autowired
    lateinit var teamEventsRepository: TeamEventsRepository

    lateinit var defaultUser: User

    lateinit var defaultCreateTeamEvent: CreateTeamEvent

    @BeforeAll()
    fun setup() {
        // create default user
        val user = User(name = "John", uid = "123456789")
        userRepository.save(user)
        defaultUser = userRepository.findById(user.id).get()

        defaultCreateTeamEvent = CreateTeamEvent(correlationId = "", title = "Default Team")
    }

    @AfterEach()
    fun cleanUp() {
        teamEventsRepository.deleteAll()
    }

    @Test
    fun `creates team and saves it`() {
        eventSourcingTeamService.update(defaultCreateTeamEvent)
        val team = eventSourcingTeamService.get(defaultCreateTeamEvent.entityId)

        assert(team.title == defaultCreateTeamEvent.title)
        assert(team.memberIds.isEmpty())
    }

    @Test
    fun `add team member`() {
        eventSourcingTeamService.update(defaultCreateTeamEvent)
        val addUserToTeamEvent =
            AddTeamMemberEvent(entityId = defaultCreateTeamEvent.entityId, correlationId = "", userId = defaultUser.id)
        eventSourcingTeamService.update(addUserToTeamEvent)
        val team = eventSourcingTeamService.get(defaultCreateTeamEvent.entityId)

        assert(team.memberIds.contains(defaultUser.id))
    }
}