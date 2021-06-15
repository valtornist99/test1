package com.quipy.server.eventsourcing.services

import com.quipy.server.query.entities.User
import com.quipy.server.eventsourcing.api.events.incoming.CreateProjectEvent
import com.quipy.server.eventsourcing.repositories.ProjectEventsRepository
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
internal class EventSourcingProjectServiceTest {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var eventSourcingProjectService: EventSourcingProjectService

    @Autowired
    lateinit var projectEventsRepository: ProjectEventsRepository

    lateinit var defaultUser: User

    @BeforeAll()
    fun setup() {
        // create default user
        val user = User(name = "John", uid = "123456789")
        userRepository.save(user)
        defaultUser = userRepository.findById(user.id).get()
    }

    @AfterEach()
    fun cleanUp() {
        projectEventsRepository.deleteAll()
    }

    @Test
    fun `creates project and saves it`() {
        val createProjectEvent =
            CreateProjectEvent(entityId = UUID.randomUUID(), title = "Test Project", creatorId = defaultUser.id)
        eventSourcingProjectService.update(createProjectEvent)
        val project = eventSourcingProjectService.get(createProjectEvent.entityId)

        assert(project.entityId == createProjectEvent.entityId.toString())
        assert(project.title == createProjectEvent.title)
        assert(project.memberIds.contains(defaultUser.id))
    }
}