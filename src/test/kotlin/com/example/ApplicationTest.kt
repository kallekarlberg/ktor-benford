package com.example

import com.example.plugins.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.testing.*
import java.io.BufferedReader
import java.io.File
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("{\"hello\":\"world\"}", bodyAsText())
        }
        val response: HttpResponse = client.post("/") {
            setBody(getResourceAsText("/accounts.txt"))
        }
        println(response.bodyAsText());
    }

    fun getResourceAsText(path: String): String? =
        object {}.javaClass.getResource(path)?.readText()
}
