package com.solidgate.api

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Url

class Api(
    private val client: HttpClient,
    private val credentials: Credentials,
    private val endpoints: Endpoints = Endpoints()
) {
    suspend fun charge(attributes: Attributes) = makeRequest("charge", attributes)
    suspend fun recurring(attributes: Attributes) = makeRequest("recurring", attributes)
    suspend fun status(attributes: Attributes) = makeRequest("status", attributes)
    suspend fun refund(attributes: Attributes) = makeRequest("refund", attributes)
    suspend fun initPayment(attributes: Attributes) = makeRequest("initPayment", attributes)
    suspend fun resign(attributes: Attributes) = makeRequest("resign", attributes)
    suspend fun auth(attributes: Attributes) = makeRequest("auth", attributes)
    suspend fun void(attributes: Attributes) = makeRequest("void", attributes)
    suspend fun settle(attributes: Attributes) = makeRequest("settle", attributes)
    suspend fun arnCode(attributes: Attributes) = makeRequest("arnCode", attributes)
    suspend fun applePay(attributes: Attributes) = makeRequest("applePay", attributes)
    suspend fun googlePay(attributes: Attributes) = makeRequest("googlePay", attributes)

    fun formUrl(attributes: Attributes): Url {
        val base64Encoded = attributes.encrypt(credentials)
        val signature = attributes.signature(credentials)

        return Url(endpoints.baseSolidGateApiUriString + "form?merchant=${credentials.merchantId}&form_data=${base64Encoded}&signature=${signature}")
    }

    private suspend fun makeRequest(path: String, attributes: Attributes): HttpResponse {

        return client.post(Url(endpoints.baseSolidGateApiUriString + path)) {
            body = attributes.toJson()
            headers.append("Content-Type", "application/json")
            headers.append("Accept", "application/json")
            headers.append("Merchant", credentials.merchantId)
            headers.append("Signature", attributes.signature(credentials))
        }
    }
}
