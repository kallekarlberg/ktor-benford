package com.kkarlberg

import com.kkarlberg.plugins.configureRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.apache.commons.math3.stat.inference.ChiSquareTest

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting(ChiSquareTest())
}
