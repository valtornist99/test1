package com.quipy.server.configuration

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource

@Configuration
class FirebaseConfiguration {
    @Bean
    fun firebaseApp(): FirebaseApp {
        val fileKey = ClassPathResource("firebase-key.json").inputStream
        val options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(fileKey))
            .build()

        return FirebaseApp.initializeApp(options)
    }
}