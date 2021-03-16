package com.solidgate.api

data class MerchantData(val paymentIntent: String, val merchant: String, val signature: String)
