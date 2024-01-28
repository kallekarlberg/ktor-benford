package com.kkarlberg.plugins

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
import org.apache.commons.math3.stat.inference.ChiSquareTest

private val logger = KotlinLogging.logger {}

//not sure if this is "the way" to do IOC in kotlin. Also, I could argue that this does not need to be injected however I understand the point about it made
fun Application.configureRouting(chiSquareTester : ChiSquareTest) {
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        gson {
        }
    }
    routing {
        post("/") {
            val inData = call.receiveText()
            try {
                val benfordSeries = BenfordCalculator(chiSquareTester).processString(inData)
                call.respond(benfordSeries)
            } catch ( e : IllegalArgumentException ) {
                logger.warn { "Bad request ${e.message}" }
                call.response.status(HttpStatusCode(400, ""+e.message))
            }
        }
    }
}
