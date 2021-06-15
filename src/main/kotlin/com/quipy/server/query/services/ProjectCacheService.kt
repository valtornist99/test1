package com.quipy.server.query.services

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.quipy.server.eventsourcing.entities.ProjectEventSourcingEntity
import com.quipy.server.query.entities.Project
import com.quipy.server.query.updateWithLatestVersion
import com.quipy.server.query.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class ProjectCacheService {
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
    fun updateProjectCache(updatedProject: ProjectEventSourcingEntity) {
        val members = updatedProject.memberIds.mapNotNull { userRepository.findById(it).get() }

        val project = Project(
            id = updatedProject.entityId,
            title = updatedProject.title,
            members = members,
            version = updatedProject.version
        )

        mongoTemplate.updateWithLatestVersion(project)
    }
}