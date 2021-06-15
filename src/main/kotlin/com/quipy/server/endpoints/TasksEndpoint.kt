package com.quipy.server.endpoints

import com.quipy.server.query.entities.Task
import com.quipy.server.query.repositories.TaskQueryRepository
import com.quipy.server.security.AuthenticationProvider
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import javax.annotation.security.RolesAllowed

@RestController
@RequestMapping("/tasks")
class TasksEndpoint(
    val taskQueryRepository: TaskQueryRepository
) {
    companion object {
        val logger = LoggerFactory.getLogger(TasksEndpoint::class.java)
    }

    @Autowired
    lateinit var authenticationProvider: AuthenticationProvider

    @RolesAllowed("USER")
    @GetMapping
    fun getUserTasks(@RequestParam userId: String): List<Task> {
        logger.info("Authenticated: ${authenticationProvider.user}")
        return taskQueryRepository.findByAssignedToId(userId)
    }
}