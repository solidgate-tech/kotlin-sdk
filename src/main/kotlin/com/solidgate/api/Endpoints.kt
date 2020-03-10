package com.solidgate.api

data class Endpoints(
    val baseSolidGateApiUriString: String = Api.BASE_SOLID_GATE_API_URI,
    val baseReconciliationsApiUriString: String = Api.BASE_RECONCILIATION_API_URI
)
