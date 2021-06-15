package com.quipy.server.security

data class User(
    val id: String,
    val uid: String,
    val name: String,
    val role: String
)