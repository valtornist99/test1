package com.quipy.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QuipyServerApplication

fun main(args: Array<String>) {
    runApplication<QuipyServerApplication>(*args)
}
