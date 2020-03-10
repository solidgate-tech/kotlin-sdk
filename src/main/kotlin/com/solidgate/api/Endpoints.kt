package com.solidgate.api

data class Endpoints(
    val baseSolidGateApiUriString: String = BASE_SOLID_GATE_API_URI,
    val baseReconciliationsApiUriString: String = BASE_RECONCILIATION_API_URI
) {
    companion object {
        const val BASE_SOLID_GATE_API_URI = "https://pay.solidgate.com/api/v1/"
        const val BASE_RECONCILIATION_API_URI = "https://reports.solidgate.com/"
    }
}
