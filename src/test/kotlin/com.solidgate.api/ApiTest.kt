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

    private val attributes = Attributes(mapOf("amount" to 100, "currency" to "USD"))
    private val credentials = Credentials("unicorn", "20c20ee3-4173-4daa-87e5-dbcce8c7949d")

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
        credentials,
        Endpoints("http://localhost/", "http://localhost/")
    )

    @Test
    fun testGenerateSignature() {
        assertEquals(
            "MjY3MzBjNDAwMDllZjIxMGMzYjQ3Mzg5ZjNiYjE4MmNhOTNkYjliNGIzMzUxNTU2M2E1ZmUzMDVlZGM0MTBkZDE2YzE3ZWVjNDI3MDkxNWFkYzFlYzVjNzc5NDI0M2NmYjZiYTRlZDUxMDlkZDlhYWM1MmUzZTAzYTFlNjIxNGU=",
            Crypto.sign(attributes.toJson(), Credentials("test", "secret"))
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
        assertEquals(credentials.merchantId, response.headers["Merchant"])
        assertEquals(
            "NGJlYmZjMzA2YzgxZWE3ODZkNTYxMGQwNTM3MTc5NDNkYTVlMjMxYmFlNDRjMGI2NDliZjg4ZWNhZmM0MGZmMmQyNDFjMmY3ZjExNjA1M2Q2YzM1OGMzY2RhZWU4YjczMDVhZTA4ODg4OGVjNGI1M2I1ZTJjMGJjNzgwZDE1YmY=",
            response.headers["Signature"]
        )
    }
}
