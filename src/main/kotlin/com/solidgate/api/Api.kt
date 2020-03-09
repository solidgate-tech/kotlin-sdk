package com.solidgate.api

import com.google.gson.Gson
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Url
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex

data class Credentials(val merchantId: String, val privateKey: String)
data class Endpoints(
    val baseSolidGateApiUriString: String = Api.BASE_SOLID_GATE_API_URI,
    val baseReconciliationsApiUriString: String = Api.BASE_RECONCILIATION_API_URI
)

class Api(private val client: HttpClient, private val credentials: Credentials, private val endpoints: Endpoints = Endpoints()) {

    private suspend fun makeRequest(path: String, attributes: Map<String, Any?>): HttpResponse {

        val bodyString = Gson().toJson(attributes)

        return client.post(Url(endpoints.baseSolidGateApiUriString + path)) {
            body = bodyString
            headers.append("Content-Type", "application/json")
            headers.append("Accept", "application/json")
            headers.append("Merchant", credentials.merchantId)
            headers.append("Signature", generateSignature(bodyString, credentials.merchantId, credentials.privateKey))
        }
    }

    suspend fun charge(attributes: Map<String, Any?>) = makeRequest("charge", attributes)
    suspend fun recurring(attributes: Map<String, Any?>) = makeRequest("recurring", attributes)
    suspend fun status(attributes: Map<String, Any?>) = makeRequest("status", attributes)
    suspend fun refund(attributes: Map<String, Any?>) = makeRequest("refund", attributes)
    suspend fun initPayment(attributes: Map<String, Any?>) = makeRequest("initPayment", attributes)
    suspend fun resign(attributes: Map<String, Any?>) = makeRequest("resign", attributes)
    suspend fun auth(attributes: Map<String, Any?>) = makeRequest("auth", attributes)
    suspend fun void(attributes: Map<String, Any?>) = makeRequest("void", attributes)
    suspend fun settle(attributes: Map<String, Any?>) = makeRequest("settle", attributes)
    suspend fun arnCode(attributes: Map<String, Any?>) = makeRequest("arnCode", attributes)
    suspend fun applePay(attributes: Map<String, Any?>) = makeRequest("applePay", attributes)
    suspend fun googlePay(attributes: Map<String, Any?>) = makeRequest("googlePay", attributes)

    companion object {
        const val BASE_SOLID_GATE_API_URI = "https://pay.solidgate.com/api/v1/"
        const val BASE_RECONCILIATION_API_URI = "https://reports.solidgate.com/"

        private const val HMAC_SHA512 = "HmacSHA512"

        fun generateSignature(bodyString: String, merchantId: String, privateKey: String): String {
            val text: String = merchantId + bodyString + merchantId
            val keyBytes: ByteArray = privateKey.toByteArray(Charsets.UTF_8)
            val sks = SecretKeySpec(keyBytes, HMAC_SHA512)
            val mac = Mac.getInstance(HMAC_SHA512)

            mac.init(sks)

            val macFinal = mac.doFinal(text.toByteArray(Charsets.UTF_8))

            return Base64.encodeBase64String(Hex.encodeHexString(macFinal).toByteArray(Charsets.UTF_8))
        }
    }
}
