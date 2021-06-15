package com.quipy.server.services

import com.quipy.server.bot.QuipyBot
import com.quipy.server.eventsourcing.api.events.incoming.AddCommentQuestionEvent
import com.quipy.server.eventsourcing.api.events.incoming.AssignUserQuestionEvent
import com.quipy.server.eventsourcing.api.events.incoming.CreateQuestionEvent
import com.quipy.server.eventsourcing.repositories.QuestionEventsRepository
import com.quipy.server.eventsourcing.services.EventSourcingQuestionService
import com.quipy.server.query.repositories.QuestionQueryRepository
import com.quipy.server.query.repositories.UserRepository
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
internal class EventSourcingQuestionEventSourcingEntityServiceTest {

    @Autowired
    lateinit var eventSourcingQuestionService: EventSourcingQuestionService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var questionQueryRepository: QuestionQueryRepository

    @Autowired
    lateinit var questionEventsRepository: QuestionEventsRepository

    @Test
    fun testSaveEvents() {
//        val createUserEvent = CreateUserEvent(
//            entityId = UUID.randomUUID(),
//            correlationId = "", // todo sukhoa default?
//            displayName = "Andrey Suchovitsky",
//            uid = "123456789"
//        )

        val questionId = UUID.randomUUID()
        val projectId = UUID.randomUUID().toString()


        QuipyBot.botUserUuid

        val createEvent = CreateQuestionEvent(
            entityId = questionId,
            correlationId = projectId,
            title = "Example Question",
            description = "Example description",
            isAnonymous = false,
            authorId = UUID.fromString(QuipyBot.botUserUuid),
            meetingId = UUID.randomUUID(),
            assignedToUserIds = emptyList()
        )

        val addCommentQuestionEvent = AddCommentQuestionEvent(
            entityId = questionId,
            correlationId = projectId,
            commentContent = "Андрюшкин коммент",
            authorId = createEvent.authorId,
            commentId = UUID.randomUUID().toString()
        )

        val addCommentQuestionEvent2 = AddCommentQuestionEvent(
            entityId = questionId,
            correlationId = projectId,
            commentContent = "другой Андрюшкин коммент",
            authorId = createEvent.authorId,
            commentId = UUID.randomUUID().toString()
        )

        val assignUserQuestionEvent = AssignUserQuestionEvent(
            entityId = questionId,
            correlationId = projectId,
            userIds = listOf(createEvent.authorId.toString())
        )

        val assignUserQuestionEvent2 = AssignUserQuestionEvent(
            entityId = questionId,
            correlationId = projectId,
            userIds = listOf()
        )

        eventSourcingQuestionService.update(createEvent)
        eventSourcingQuestionService.update(addCommentQuestionEvent)
        eventSourcingQuestionService.update(addCommentQuestionEvent2)
        eventSourcingQuestionService.update(assignUserQuestionEvent)
        eventSourcingQuestionService.update(assignUserQuestionEvent2)

        println(
            eventSourcingQuestionService.get(createEvent.entityId)
        )

        await.atMost(2, TimeUnit.SECONDS).until {
            questionQueryRepository.findByIdAndVersion(createEvent.entityId.toString(), 5).isPresent
        }
    }
}