package com.quipy.server.endpoints

import com.quipy.server.query.entities.Question
import com.quipy.server.query.repositories.QuestionQueryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/questions")
class QuestionsEndpoint(
    val questionQueryRepository: QuestionQueryRepository
) {
    @GetMapping
    fun getUserQuestions(@RequestParam userId: String): List<Question> {
        return questionQueryRepository.findByAssignedToId(userId)
    }
}