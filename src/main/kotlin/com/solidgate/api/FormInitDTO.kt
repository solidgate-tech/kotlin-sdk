package com.solidgate.api

data class FormInitDTO(val paymentIntent: String, val merchant: String, val signature: String)
