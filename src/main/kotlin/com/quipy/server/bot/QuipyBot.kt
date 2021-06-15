package com.quipy.server.bot

import com.quipy.server.query.entities.User
import com.quipy.server.query.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class QuipyBot {
    companion object {
        const val botUserUuid = "9e204760-b6a5-4132-b50f-5ffc5a328544"
    }

    @Autowired
    lateinit var userRepository: UserRepository

    @PostConstruct
    fun init() {
        if (!userRepository.existsById(botUserUuid)) {
            userRepository.save(User(botUserUuid, "Quipy Bot", botUserUuid))
        }
    }

}