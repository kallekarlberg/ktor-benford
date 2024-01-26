package com.example

import com.example.plugins.configureRouting
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

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
            setBody(getAccountFileAsText())
        }
        println(response.bodyAsText())
    }

    private fun getAccountFileAsText(): String? =
        object {}.javaClass.getResource("/accounts.txt")?.readText()
}
