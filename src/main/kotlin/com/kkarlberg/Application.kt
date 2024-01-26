package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

// Thanks for submitting your solution...
// I have a couple of comments, could you please do one iteration based on them?
// TODO 1. could you please fix the packages there are warnings on the top of each file
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureRouting()
}
