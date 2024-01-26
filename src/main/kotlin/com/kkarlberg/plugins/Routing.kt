package com.example.plugins

import com.kkarlberg.application.BenfordCalculator
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

// TODO 2. could you please format the code
fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        gson {
        }
    }
    routing {
        post("/") {
            val inData = call.receiveText()
            try {
                val benfordSeries = BenfordCalculator().processString(inData)
                call.respond(benfordSeries)
            } catch ( e : IllegalArgumentException ) {
                logger.warn { "Bad request ${e.message}" }
                call.response.status(HttpStatusCode(400, ""+e.message))
            }
        }
    }
}
