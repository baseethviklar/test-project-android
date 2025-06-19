package com.orbits.testproject.main.model

import kotlinx.serialization.Serializable


@Serializable
data class GetBarcodeRequestModel(
    val Gate_Code: String? = null,
    val Error_Code: Int? = null,
    val Error_Msg: String? = null,
    val Printer_Status: Int? = null,
    val Printer_Status_Str_En: String? = null,
    val Printer_Status_Str_Ar: String? = null,

)
