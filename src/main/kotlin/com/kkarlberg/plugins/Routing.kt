package com.example.plugins

import com.kkarlberg.application.BenfordCalculator
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        gson {
        }
    }
    routing {
        get("/") {
            call.respond(mapOf("hello" to "world"))
        }
        post("/") {
            val inData = call.receiveText()
            val benfordSeries = BenfordCalculator().processString(inData)
            call.respond(benfordSeries)
        }
    }
}
