package com.solidgate.api

import com.google.gson.Gson
import java.util.Base64.getUrlEncoder

data class Attributes(val attributes: Map<String, Any?>) {
    fun toJson(): String {
        return Gson().toJson(attributes)
    }

    fun signature(credentials: Credentials): String {
        val data = credentials.merchantId + this.toJson() + credentials.merchantId
        val hmac = Crypto.hmac(data, credentials.privateKey)

        return Crypto.base64encode(hmac)
    }

    fun encrypt(credentials: Credentials): String {
        return getUrlEncoder()
            .encodeToString(Crypto.encrypt(this.toJson().toByteArray(), credentials.privateKey))
    }
}
