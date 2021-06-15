package com.quipy.server.notification.controller

import com.quipy.server.notification.service.SeenService
import com.quipy.server.notification.dto.SeenDto
import com.quipy.server.query.repositories.UserRepository
import com.quipy.server.security.AuthenticationProvider
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.web.bind.annotation.*
import javax.annotation.security.RolesAllowed

@RestController
@RequestMapping("/seen")
class SeenController(
    val userRepository: UserRepository,
    var authenticationProvider: AuthenticationProvider,
    var mongoTemplate: MongoTemplate,
    var seenService: SeenService
) {
    @RolesAllowed("USER")
    @PostMapping
    fun seen(@RequestParam questionId: String) {
        val userId = authenticationProvider.user.id
        seenService.threadSeenByUser(questionId, userId)
    }

    @RolesAllowed("USER")
    @GetMapping
    fun seenByProject(@RequestParam projectId: String): List<SeenDto> {
        return seenService.getSeensByProject(authenticationProvider.user.id, projectId)
    }
}