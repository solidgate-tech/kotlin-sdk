package com.solidgate.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readBytes
import io.ktor.util.KtorExperimentalAPI
import org.junit.*
import kotlinx.coroutines.*
import org.junit.Assert.assertEquals

@KtorExperimentalAPI
class ApiTest {

    private val attributes = mapOf("amount" to 100, "currency" to "USD")

    private val mockClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->
                val responseHeaders = request.headers

                respond(String(request.body.toByteArray()), headers = responseHeaders)
            }
        }
    }

    private val api = Api(
        mockClient,
        Credentials("test", "secret"),
        Endpoints("http://localhost/", "http://localhost/")
    )

    @Test
    fun testGenerateSignature() {
        val bodyString = """{"test":"json"}"""
        val signature = Api.generateSignature(bodyString, "test", "secret")

        assertEquals(
            "MWRmYTk1NmY2ZWVhYzZjMjk4OWNkZTgwMWFiMmRkZDBjNjZhODJkOTY4ZTU1MTViNDRiMTZhM2UxZjUzNzA0NjE3ZWFlZGI1YmM1ODBhMmU2ZjBjNmM4ODViNmM4NzI4OTE1NWI3ODEzOTViMzlhYjFiZGM4MzhiM2NhYmUwYmU=",
            signature
        )
    }

    @Test
    fun testApiMethods() = runBlocking {
        checkResponse(api.charge(attributes))
        checkResponse(api.recurring(attributes))
        checkResponse(api.status(attributes))
        checkResponse(api.refund(attributes))
        checkResponse(api.initPayment(attributes))
        checkResponse(api.resign(attributes))
        checkResponse(api.auth(attributes))
        checkResponse(api.void(attributes))
        checkResponse(api.settle(attributes))
        checkResponse(api.arnCode(attributes))
        checkResponse(api.applePay(attributes))
        checkResponse(api.googlePay(attributes))
    }

    private suspend fun checkResponse(response: HttpResponse) {
        val responseBody = String(response.readBytes())

        assertEquals("""{"amount":100,"currency":"USD"}""", responseBody)
        assertEquals("test", response.headers["Merchant"])
        assertEquals(
            "MjY3MzBjNDAwMDllZjIxMGMzYjQ3Mzg5ZjNiYjE4MmNhOTNkYjliNGIzMzUxNTU2M2E1ZmUzMDVlZGM0MTBkZDE2YzE3ZWVjNDI3MDkxNWFkYzFlYzVjNzc5NDI0M2NmYjZiYTRlZDUxMDlkZDlhYWM1MmUzZTAzYTFlNjIxNGU=",
            response.headers["Signature"]
        )
    }
}
