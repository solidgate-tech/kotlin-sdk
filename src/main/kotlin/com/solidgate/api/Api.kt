package com.solidgate.api

import io.ktor.client.HttpClient
import io.ktor.client.features.ResponseException
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import kotlinx.coroutines.delay

class Api(
    private val client: HttpClient,
    private val credentials: Credentials,
    private val endpoints: Endpoints = Endpoints()
) {
    companion object {
        const val DEFAULT_RETRY_DELAY = 5000L

        const val MAX_RETRIES = 3

        val RETRYABLE_STATUS_CODES = setOf(
            HttpStatusCode.TooManyRequests,
            HttpStatusCode.ServiceUnavailable
        )
    }

    suspend fun recurring(attributes: Attributes) = makeRequest("recurring", attributes)
    suspend fun status(attributes: Attributes) = makeRequest("status", attributes)
    suspend fun refund(attributes: Attributes) = makeRequest("refund", attributes)
    suspend fun resign(attributes: Attributes) = makeRequest("resign", attributes)
    suspend fun auth(attributes: Attributes) = makeRequest("auth", attributes)
    suspend fun void(attributes: Attributes) = makeRequest("void", attributes)
    suspend fun settle(attributes: Attributes) = makeRequest("settle", attributes)
    suspend fun arnCode(attributes: Attributes) = makeRequest("arn-code", attributes)
    suspend fun applePay(attributes: Attributes) = makeRequest("apple-pay", attributes)
    suspend fun googlePay(attributes: Attributes) = makeRequest("google-pay", attributes)

    fun formMerchantData(attributes: Attributes): FormInitDTO {
        val base64Encoded = attributes.encrypt(credentials)
        val signature = Crypto.sign(base64Encoded, credentials)

        return FormInitDTO(base64Encoded, credentials.merchantId, signature)
    }

    fun formUpdate(attributes: Attributes): FormUpdateDTO {
        val base64Encoded = attributes.encrypt(credentials)
        val signature = Crypto.sign(base64Encoded, credentials)

        return FormUpdateDTO(base64Encoded, signature)
    }

    fun formResign(attributes: Attributes): FormResignDTO {
        val base64Encoded = attributes.encrypt(credentials)
        val signature = Crypto.sign(base64Encoded, credentials)

        return FormResignDTO(base64Encoded, credentials.merchantId, signature)
    }

    private suspend fun makeRequest(path: String, attributes: Attributes): HttpResponse {
        var lastResponse: HttpResponse? = null

        for (attempt in 1..MAX_RETRIES) {
            val response = try {
                client.post<HttpResponse>(Url(endpoints.baseSolidGateApiUriString + path)) {
                    body = attributes.toJson()
                    headers.append("Content-Type", "application/json")
                    headers.append("Accept", "application/json")
                    headers.append("Merchant", credentials.merchantId)
                    headers.append("Signature", Crypto.sign(attributes.toJson(), credentials))
                    headers.append("User-Agent", "Kotlin-SDK-0.6.0")
                }
            } catch (e: ResponseException) {
                e.response
            }

            if (response.status !in RETRYABLE_STATUS_CODES || attempt == MAX_RETRIES) {
                return response
            }

            lastResponse = response
            delay(DEFAULT_RETRY_DELAY)
        }

        return lastResponse!!
    }
}
