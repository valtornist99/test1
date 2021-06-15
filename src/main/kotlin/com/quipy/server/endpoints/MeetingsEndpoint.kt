package com.quipy.server.endpoints

import com.quipy.server.query.entities.Meeting
import com.quipy.server.query.repositories.MeetingQueryRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/meetings")
class MeetingsEndpoint(
    val meetingQueryRepository: MeetingQueryRepository
) {
    @GetMapping
    fun getUserQuestions(@RequestParam userId: String): List<Meeting> {
        return meetingQueryRepository.findByParticipantsId(userId)
    }
}