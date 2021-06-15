package com.quipy.server.endpoints

import com.quipy.server.query.entities.User
import com.quipy.server.query.repositories.UserRepository
import com.quipy.server.security.AuthenticationProvider
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.annotation.security.RolesAllowed

data class CreateUserDTO(val displayName: String, val uid: String)

@RestController
@RequestMapping("/users")
class UsersEndpoint(
    val userRepository: UserRepository,
    var authenticationProvider: AuthenticationProvider
) {
    @PostMapping
    fun createUser(@RequestBody body: CreateUserDTO): ResponseEntity<Unit> {
        if (userRepository.existsByUid(body.uid)) {
            return ResponseEntity.badRequest().build()
        }

        val user = User(name = body.displayName, uid = body.uid)
        userRepository.save(user)
        val resourceLocation = URI.create("/users/${user.id}")
        return ResponseEntity.created(resourceLocation).build()
    }

    @GetMapping("/login")
    @RolesAllowed("GUEST", "USER")
    fun login(): com.quipy.server.security.User {
        return authenticationProvider.user
    }

    @GetMapping
    @RolesAllowed("USER")
    fun getUser(
        @RequestParam(required = false) id: String?
    ): ResponseEntity<Any> {
        if (id != null) {
            return ResponseEntity.ok(userRepository.findById(id).orElse(null))
        }

        return ResponseEntity.ok(userRepository.findAll())
    }
}