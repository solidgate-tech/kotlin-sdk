package com.solidgate.api

import org.apache.commons.codec.binary.Hex
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class Crypto {
    companion object {

        private const val HMAC_SHA512 = "HmacSHA512"
        private const val AES_CBC_PKCS5Padding = "AES/CBC/PKCS5Padding"
        private const val AES = "AES"
        private const val KEY_SIZE = 32
        private const val IV_SIZE = 16

        fun encrypt(plaintext: ByteArray, privateKey: String): ByteArray {
            val cipher = Cipher.getInstance(AES_CBC_PKCS5Padding)
            val keySpec = SecretKeySpec(key(privateKey).encoded, AES)
            val iv = iv()
            val ivParameterSpec = IvParameterSpec(iv)
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec)
            return iv + cipher.doFinal(plaintext)
        }

        fun hmac(data: String, key: String): ByteArray {
            val sks = SecretKeySpec(key.toByteArray(), HMAC_SHA512)
            val mac = Mac.getInstance(HMAC_SHA512)

            mac.init(sks)

            return mac.doFinal(data.toByteArray())
        }

        fun base64encode(data: ByteArray): String {
            return Base64.getUrlEncoder()
                .encodeToString(Hex.encodeHexString(data).toByteArray())
        }

        private fun key(privateKey: String): SecretKey {
            return SecretKeySpec(privateKey.take(KEY_SIZE).toByteArray(), AES)
        }

        private fun iv(): ByteArray {
            val iv = ByteArray(IV_SIZE)
            val random = SecureRandom()
            random.nextBytes(iv)
            return iv
        }
    }
}
