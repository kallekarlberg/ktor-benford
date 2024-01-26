package com.example

import com.example.plugins.configureRouting
import com.google.gson.Gson
import com.kkarlberg.application.BenfordSeries
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ApplicationTest {
    @Test
    fun testPostOkString() = testApplication {
        application {
            configureRouting()
        }
        val response: HttpResponse = client.post("/") {
            setBody(getAccountFileAsText())
        }
        assertEquals(200, response.status.value)
        val benfordDataStr = response.bodyAsText()
        val benfordData = Gson().fromJson(benfordDataStr, BenfordSeries::class.java)
        assertTrue( benfordData.chiSquareTest > 0.0)
        // TODO 10. yes this test is not that useful, but you don't need to test the library anyway,
        // if you make it a dependency, you can mock it
        //ToDo add more asserts here
    }

    @Test
    fun testPostShortString() = testApplication {
        application {
            configureRouting()
        }
        val response: HttpResponse = client.post("/") {
            setBody("short1000aaa5000a4999")
        }
        assertEquals(400, response.status.value)
    }

    private fun getAccountFileAsText(): String? =
        object {}.javaClass.getResource("/accounts.txt")?.readText()
}
