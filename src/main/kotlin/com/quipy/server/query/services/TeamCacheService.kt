package com.quipy.server.query.services

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.quipy.server.eventsourcing.entities.TeamEventSourcingEntity
import com.quipy.server.query.entities.Team
import com.quipy.server.query.updateWithLatestVersion
import com.quipy.server.query.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class TeamCacheService {
    @Autowired
    lateinit var cacheUpdateEventBus: EventBus

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    @Autowired
    lateinit var userRepository: UserRepository

    @PostConstruct
    fun init() {
        cacheUpdateEventBus.register(this)
    }

    @Subscribe
    fun updateProjectCache(updatedTeam: TeamEventSourcingEntity) {
        val members = updatedTeam.memberIds.map {
            userRepository.findById(it).get()
        }

        val team = Team(
            id = updatedTeam.entityId,
            title = updatedTeam.title,
            members = members,
            version = updatedTeam.version
        )

        mongoTemplate.updateWithLatestVersion(team)
    }
}