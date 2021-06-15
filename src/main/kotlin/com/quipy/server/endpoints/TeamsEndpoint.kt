package com.quipy.server.endpoints

import com.quipy.server.query.entities.User
import com.quipy.server.eventsourcing.api.events.incoming.AddTeamMemberEvent
import com.quipy.server.eventsourcing.api.events.incoming.CreateTeamEvent
import com.quipy.server.eventsourcing.services.EventSourcingTeamService
import com.quipy.server.query.repositories.TeamQueryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

data class CreateTeamDTO(val title: String)
data class AddTeamMemberDTO(val userId: String)

@RestController
@RequestMapping("/teams")
class TeamsEndpoint(
    val eventSourcingTeamService: EventSourcingTeamService,
    val teamQueryRepository: TeamQueryRepository
) {
    @GetMapping
    fun getTeams() = teamQueryRepository.findAll()

    @GetMapping("/{teamId}")
    fun getTeam(@PathVariable teamId: String) = teamQueryRepository.findById(teamId)

    @PostMapping
    fun createTeam(@RequestBody body: CreateTeamDTO): ResponseEntity<Unit> {
        val event = CreateTeamEvent(
            entityId = UUID.randomUUID(),
            correlationId = null,
            title = body.title
        )

        eventSourcingTeamService.update(event)

        val resourceLocation = URI.create("teams/${event.entityId}")
        return ResponseEntity.created(resourceLocation).build()
    }

    @PostMapping("/{teamId}/members")
    fun addTeamMember(@RequestBody body: AddTeamMemberDTO, @PathVariable teamId: String): ResponseEntity<List<User>> {
        val event = AddTeamMemberEvent(entityId = UUID.fromString(teamId), correlationId = null, userId = body.userId)

        eventSourcingTeamService.update(event)
        val members = teamQueryRepository.findById(teamId).get().members

        return ResponseEntity.ok(members)
    }
}