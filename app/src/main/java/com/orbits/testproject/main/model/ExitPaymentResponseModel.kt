package com.orbits.testproject.main.model

import kotlinx.serialization.Serializable

@Serializable
data class ExitPaymentResponseModel(
    val Remark: String? = null,
    val amountCharged: String? = null,
    val barcode: String? = null,
    val depositAvailable: String? = null,
    val depositDeducted: String? = null,
    val entryGate: String? = null,
    val entryTime: String? = null,
    val exitTime: String? = null,
    val invoiceFiscal: String? = null,
    val invoiceNo: String? = null,
    val jsonReturnStatus: ExitPaymentJsonReturnStatus? = null,
    val level: String? = null,
    val payTime: String? = null,
    val vatAmount: String? = null
)

@Serializable
data class ExitPaymentJsonReturnStatus(
    val errMessage: String? = null,
    val reason: String? = null,
    val success: Boolean? = null
)