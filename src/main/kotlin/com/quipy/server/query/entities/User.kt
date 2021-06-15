package com.quipy.server.query.entities

import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@Document(collection = "users")
data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val uid: String
)
