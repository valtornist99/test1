package com.quipy.server.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.WriteResultChecking
import javax.annotation.PostConstruct

@Configuration
class MongoTemplateConfiguration {
    @Autowired
    lateinit var preconfiguredTemplate: MongoTemplate

    @PostConstruct
    fun mongoTemplate() {
        preconfiguredTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION)
    }

}