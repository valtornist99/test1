package com.quipy.server.services

import com.quipy.server.bot.QuipyBot
import com.quipy.server.query.entities.User
import com.quipy.server.eventsourcing.api.events.incoming.AddCommentQuestionEvent
import com.quipy.server.eventsourcing.api.events.incoming.AssignUserQuestionEvent
import com.quipy.server.eventsourcing.api.events.incoming.CreateQuestionEvent
import com.quipy.server.eventsourcing.services.EventSourcingQuestionService
import com.quipy.server.notification.repository.ThreadsRepository
import com.quipy.server.notification.service.SeenService
import com.quipy.server.query.repositories.QuestionQueryRepository
import com.quipy.server.query.repositories.UserRepository
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
internal class SeenTest {
    @Autowired
    lateinit var eventSourcingQuestionService: EventSourcingQuestionService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var questionQueryRepository: QuestionQueryRepository

    @Autowired
    lateinit var seenService: SeenService

    @Autowired
    lateinit var threadRepository: ThreadsRepository

    @Test
    fun testSaveEvents() {
        val andreyUser = User(
            name = "Andrey Test",
            uid = "12345"
        )

        userRepository.save(andreyUser)

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


        val addCommentQuestionEvent3 = AddCommentQuestionEvent(
            entityId = questionId,
            correlationId = projectId,
            commentContent = "comment",
            authorId = UUID.fromString(andreyUser.id),
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

        await.atMost(1, TimeUnit.SECONDS).until {
            threadRepository.findById(questionId.toString()).map {
                it.numberOfComments == 2
            }.orElse(false)
        }

        seenService.threadSeenByUser(questionId.toString(), QuipyBot.botUserUuid)

        await.atMost(1, TimeUnit.SECONDS).until {
            seenService.getSeensForQuestions(QuipyBot.botUserUuid, setOf(questionId.toString()))
                .first()
                .numberOfUnreadComments == 0
        }

        eventSourcingQuestionService.update(addCommentQuestionEvent3)
        eventSourcingQuestionService.update(assignUserQuestionEvent)
        eventSourcingQuestionService.update(assignUserQuestionEvent2)

        println(
            eventSourcingQuestionService.get(createEvent.entityId)
        )


        await.atMost(1, TimeUnit.SECONDS).until {
            seenService.getSeensForQuestions(andreyUser.id, setOf(questionId.toString()))
                .first()
                .numberOfUnreadComments == 0
        }
        await.atMost(1, TimeUnit.SECONDS).until {
            seenService.getSeensForQuestions(QuipyBot.botUserUuid, setOf(questionId.toString()))
                .first()
                .numberOfUnreadComments == 1
        }
    }
}