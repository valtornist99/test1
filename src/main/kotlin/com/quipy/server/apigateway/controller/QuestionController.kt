package com.quipy.server.apigateway.controller

import com.quipy.server.notification.service.SeenService
import com.quipy.server.query.entities.Question
import com.quipy.server.query.repositories.QuestionQueryRepository
import com.quipy.server.query.repositories.UserRepository
import com.quipy.server.security.AuthenticationProvider
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.web.bind.annotation.*
import javax.annotation.security.RolesAllowed

@RestController
@RequestMapping("api/question")
class QuestionController(
    val userRepository: UserRepository,
    var authenticationProvider: AuthenticationProvider,
    var mongoTemplate: MongoTemplate,
    var seenService: SeenService,
    val questionQueryRepository: QuestionQueryRepository

) {
    @RolesAllowed("USER")
    @GetMapping
    fun getUserQuestions(): List<Question> {
        val questionsById =
            questionQueryRepository.findAllByWatchersIdIn(authenticationProvider.user.id).associateBy { it.id }

        return seenService.getSeensForQuestions(authenticationProvider.user.id, questionsById.keys)
            .mapNotNull { seen ->
                questionsById[seen.questionId]?.also {
                    it.numberOfUnreadComments = seen.numberOfUnreadComments // todo sukhoa think of introducing some dto instead of var field in question
                }
            }
    }
}