package com.orbits.testproject.main.model

import kotlinx.serialization.Serializable

@Serializable
data class GetBarcodeResponseModel(
    val barcode: String? = null,
    val jsonReturnStatus: JsonReturnStatus? = null,
    val serverTime: String? = null
)

@Serializable
data class JsonReturnStatus(
    val errMessage: String? = null,
    val reason: String? = null,
    val success: Boolean? = null
)
