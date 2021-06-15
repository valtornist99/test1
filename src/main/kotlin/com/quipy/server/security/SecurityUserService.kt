package com.quipy.server.security

import com.google.firebase.auth.FirebaseAuth
import com.quipy.server.query.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SecurityUserService {
    @Autowired
    lateinit var userRepository: UserRepository

    companion object {
        const val ROLE_GUEST = "GUEST"
        const val ROLE_USER = "USER"
        val guestUser =
            User(id = "unauthorized_user", uid = "unauthorized_user", name = "unauthorized_user", role = ROLE_GUEST)
    }


    fun getUserByFirebaseToken(token: String?): User? = when (token) {
        null, "" -> {
            guestUser
        }
        "test" -> {
            User(id = "test_user", uid = "test_user", name = "test_user", ROLE_USER) // todo sukhoa remove in prod
        }
        else -> {
            try {
                val uid = FirebaseAuth.getInstance().verifyIdToken(token).uid
                userRepository.findByUid(uid)
                    .map { User(id = it.id, uid = it.uid, name = it.name, role = ROLE_USER) }
                    .orElse(guestUser)
            } catch (e: Exception) {
                println(e)
                null // todo sukhoa: introduce sealed class
            }
        }
    }
}