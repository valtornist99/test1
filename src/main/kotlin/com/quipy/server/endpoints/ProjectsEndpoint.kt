package com.quipy.server.endpoints

import com.quipy.server.eventsourcing.api.events.incoming.*
import com.quipy.server.eventsourcing.services.EventSourcingMeetingService
import com.quipy.server.eventsourcing.services.EventSourcingProjectService
import com.quipy.server.eventsourcing.services.EventSourcingQuestionService
import com.quipy.server.eventsourcing.services.EventSourcingTaskService
import com.quipy.server.query.entities.Meeting
import com.quipy.server.query.entities.Project
import com.quipy.server.query.entities.Question
import com.quipy.server.query.entities.Task
import com.quipy.server.query.repositories.MeetingQueryRepository
import com.quipy.server.query.repositories.ProjectQueryRepository
import com.quipy.server.query.repositories.QuestionQueryRepository
import com.quipy.server.query.repositories.TaskQueryRepository
import com.quipy.server.security.AuthenticationProvider
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*
import javax.annotation.security.RolesAllowed

data class CreateProjectDTO(val title: String)

data class EditProjectTitleDTO(val title: String)

data class AddProjectMemberDTO(val userId: String)

data class AskQuestionDTO(
    val title: String,
    val description: String = "",
    val date: Long,
    val authorId: String,
    val assignedToUserIds: List<String>?,
    val isAnonymous: Boolean = true,
    val meetingId: String?,
    val parentQuestionId: String?,
)

data class UpdateQuestionDTO(
    val title: String?,
    val description: String?,
    val assignedToUserIds: List<String>?,
    val isAnonymous: Boolean?
)

data class AddCommentQuestionDTO(
    val content: String,
    val authorId: String,
)

data class PlanMeetingDTO(
    val title: String,
    val date: Long,
    val teamId: String,
    val participantIds: List<String>,
    val questionIds: List<String>
)

data class UpdateMeetingDTO(
    val title: String?,
    val date: Long?,
    val teamId: String?,
    val participantIds: List<String>?,
    val questionIds: List<String>?
)

data class AddTaskDTO(
    val title: String,
    val description: String?,
    val assignedToUserId: String?,
    val associatedQuestionId: String?,
    val associatedMeetingId: String?,
)

data class ChangeTaskStatusDTO(
    val status: String
)

data class UpdateTaskDTO(
    val title: String?,
    val description: String?,
    val assignedToUserId: String?,
    val associatedQuestionId: String?,
    val associatedMeetingId: String?,
)

@RestController
@RequestMapping("/projects")
@Tag(name = "projects")
class ProjectsEndpoint(
    val questionQueryRepository: QuestionQueryRepository,
    val eventSourcingQuestionService: EventSourcingQuestionService,
    val projectQueryRepository: ProjectQueryRepository,
    val eventSourcingProjectService: EventSourcingProjectService,
    val taskQueryRepository: TaskQueryRepository,
    val eventSourcingTaskService: EventSourcingTaskService,
    val meetingQueryRepository: MeetingQueryRepository,
    val eventSourcingMeetingService: EventSourcingMeetingService,
    var authenticationProvider: AuthenticationProvider
) {

    @GetMapping
    @RolesAllowed("USER")
    fun getProjects(): List<Project> {
        val userId = authenticationProvider.user.id
        return projectQueryRepository.findByMembersId(userId)
    }

    @PostMapping
    fun createProject(@RequestBody body: CreateProjectDTO): ResponseEntity<Unit> {
        val createProject = CreateProjectEvent(
            entityId = UUID.randomUUID(),
            title = body.title,
            correlationId = null,
            creatorId = authenticationProvider.user.id
        )
        eventSourcingProjectService.update(createProject)

        val resourceLocation = URI.create("/projects/${createProject.entityId}")
        return ResponseEntity.created(resourceLocation).build()
    }

    @PutMapping("{projectId}/title")
    fun editProjectTitle(
        @RequestBody body: EditProjectTitleDTO,
        @PathVariable projectId: String
    ): ResponseEntity<Unit> {
        val editProjectTitleEvent = EditProjectTitleEvent(entityId = UUID.fromString(projectId), title = body.title)
        eventSourcingProjectService.update(editProjectTitleEvent)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("{projectId}/members")
    fun addProjectMember(
        @RequestBody body: AddProjectMemberDTO,
        @PathVariable projectId: String
    ): ResponseEntity<Unit> {
        val event = AddProjectMemberEvent(
            entityId = UUID.fromString(projectId),
            correlationId = null,
            userId = body.userId
        )

        eventSourcingProjectService.update(event)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{projectId}/questions")
    fun getQuestions(@PathVariable projectId: String): List<Question> =
        questionQueryRepository.findAllByProjectId(projectId)

    @GetMapping("/{projectId}/questions/{questionId}")
    fun getQuestion(@PathVariable projectId: String, @PathVariable questionId: String?): Question? {
        if (questionId === null) {
            return null
        }

        return questionQueryRepository.findByProjectIdAndId(projectId, questionId)
    }

    @PostMapping("/{projectId}/questions")
    fun askQuestion(
        @RequestBody body: AskQuestionDTO,
        @PathVariable projectId: String
    ): ResponseEntity<Unit> {
        val event = CreateQuestionEvent(
            correlationId = projectId,
            title = body.title,
            description = body.description,
            authorId = UUID.fromString(body.authorId),
            assignedToUserIds = body.assignedToUserIds,
            isAnonymous = body.isAnonymous
        )

        eventSourcingQuestionService.update(event)

        val resourceLocation = URI.create("projects/$projectId/questions/${event.entityId}")
        return ResponseEntity.created(resourceLocation).build()
    }

    @PatchMapping("/{projectId}/questions/{questionId}")
    fun updateQuestion(
        @RequestBody body: UpdateQuestionDTO,
        @PathVariable projectId: String,
        @PathVariable questionId: String
    ): ResponseEntity<Question> {
        val event = UpdateQuestionEvent(
            entityId = UUID.fromString(questionId),
            correlationId = projectId,
            title = body.title,
            description = body.description,
            assignedToUserIds = body.assignedToUserIds,
            isAnonymous = body.isAnonymous
        )

        eventSourcingQuestionService.update(event)

        val question = questionQueryRepository.findById(questionId).get()
        return ResponseEntity.ok(question)
    }

    @PostMapping("/{projectId}/questions/{questionId}/comments")
    fun commentQuestion(
        @RequestBody addCommentQuestion: AddCommentQuestionDTO,
        @PathVariable projectId: String,
        @PathVariable questionId: String
    ): ResponseEntity<Unit> {
        val event = AddCommentQuestionEvent(
            entityId = UUID.fromString(questionId),
            correlationId = projectId,
            commentId = UUID.randomUUID().toString(),
            commentContent = addCommentQuestion.content,
            authorId = UUID.fromString(addCommentQuestion.authorId)
        )

        eventSourcingQuestionService.update(event)

        val resourceLocation = URI.create("/$projectId/questions/$questionId/comments/${event.commentId}")
        return ResponseEntity.created(resourceLocation).build()
    }

    @PostMapping("/{projectId}/questions/{questionId}/comments/{commentId}/resolution")
    fun markCommentAsAnswer(
        @PathVariable projectId: String,
        @PathVariable questionId: String,
        @PathVariable commentId: String
    ): ResponseEntity<Unit> {
        val event = MarkCommentAsAnswerEvent(
            entityId = UUID.fromString(questionId),
            correlationId = projectId,
            commentId = commentId
        )

        eventSourcingQuestionService.update(event)

        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{projectId}/questions/{questionId}/comments/{commentId}/resolution")
    fun unmarkCommentAsAnswer(
        @PathVariable projectId: String,
        @PathVariable questionId: String,
        @PathVariable commentId: String
    ): ResponseEntity<Unit> {
        val event = UnmarkCommentAsAnswerEvent(
            entityId = UUID.fromString(questionId),
            correlationId = projectId,
            commentId = commentId
        )

        eventSourcingQuestionService.update(event)

        return ResponseEntity.ok().build()
    }

    @GetMapping("/{projectId}/meetings")
    fun getMeetings(@PathVariable projectId: String, @RequestParam(required = false) teamId: String?): List<Meeting> {
        var results = meetingQueryRepository.findAllByProjectId(projectId)

        // filter by target team id
        if (!teamId.isNullOrBlank()) {
            results = results.filter { it.team.id == teamId }
        }

        return results
    }

    @PostMapping("/{projectId}/meetings")
    fun planMeeting(@RequestBody body: PlanMeetingDTO, @PathVariable projectId: String): ResponseEntity<Unit> {
        val event = PlanMeetingEvent(
            entityId = UUID.randomUUID(),
            correlationId = projectId,
            title = body.title,
            date = body.date,
            participantIds = body.participantIds,
            questionIds = body.questionIds,
            teamId = body.teamId
        )

        eventSourcingMeetingService.update(event)

        val location = URI.create("/$projectId/meetings/${event.entityId}")
        return ResponseEntity.created(location).build()
    }

    @PatchMapping("/{projectId}/meetings/{meetingId}")
    fun updateMeeting(
        @RequestBody body: UpdateMeetingDTO,
        @PathVariable projectId: String,
        @PathVariable meetingId: String
    ): ResponseEntity<Meeting> {
        val event = UpdateMeetingEvent(
            entityId = UUID.fromString(meetingId),
            correlationId = projectId,
            title = body.title,
            date = body.date,
            participantIds = body.participantIds,
            questionIds = body.questionIds,
            teamId = body.teamId
        )

        eventSourcingMeetingService.update(event)

        val meeting = meetingQueryRepository.findById(meetingId).get()
        return ResponseEntity.ok(meeting)
    }

    @PostMapping("/{projectId}/meetings/{meetingId}/complete")
    fun completeMeeting(@PathVariable projectId: String, @PathVariable meetingId: String): ResponseEntity<Unit> {
        val event = CompleteMeetingEvent(
            entityId = UUID.fromString(meetingId),
            correlationId = projectId
        )

        eventSourcingMeetingService.update(event)

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{projectId}/tasks")
    fun getTasks(@PathVariable projectId: String) = taskQueryRepository.findAllByProjectId(projectId)

    @GetMapping("/{projectId}/tasks/{taskId}")
    fun getTask(@PathVariable projectId: String, @PathVariable taskId: String) =
        taskQueryRepository.findByProjectIdAndId(projectId, taskId)

    @PostMapping("/{projectId}/tasks")
    fun createTask(@RequestBody body: AddTaskDTO, @PathVariable projectId: String): ResponseEntity<Unit> {
        val event = CreateTaskEvent(
            correlationId = projectId,
            assignedToUserId = body.assignedToUserId,
            title = body.title,
            description = body.description
        )

        if (!body.associatedQuestionId.isNullOrEmpty()) {
            event.associatedQuestionId = body.associatedQuestionId
        }

        if (!body.associatedMeetingId.isNullOrEmpty()) {
            event.associatedMeetingId = body.associatedMeetingId
        }

        eventSourcingTaskService.update(event)

        val resourceLocation = URI.create("/projects/${projectId}/tasks/${event.entityId}")
        return ResponseEntity.created(resourceLocation).build()
    }

    @PutMapping("/{projectId}/tasks/{taskId}/status")
    fun changeTaskStatus(
        @RequestBody body: ChangeTaskStatusDTO,
        @PathVariable projectId: String,
        @PathVariable taskId: String
    ): ResponseEntity<Unit> {
        val event = ChangeTaskStatusEvent(
            entityId = UUID.fromString(taskId),
            correlationId = projectId,
            status = body.status
        )

        eventSourcingTaskService.update(event)

        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{projectId}/tasks/{taskId}")
    fun updateTask(
        @RequestBody body: UpdateTaskDTO,
        @PathVariable projectId: String,
        @PathVariable taskId: String
    ): ResponseEntity<Task> {
        val event = UpdateTaskEvent(
            entityId = UUID.fromString(taskId),
            correlationId = projectId,
            title = body.title,
            description = body.description,
            assignedToUserId = body.assignedToUserId,
            associatedMeetingId = body.associatedMeetingId,
            associatedQuestionId = body.associatedQuestionId
        )

        eventSourcingTaskService.update(event)

        val task = taskQueryRepository.findById(taskId).get()
        return ResponseEntity.ok(task)
    }
}
