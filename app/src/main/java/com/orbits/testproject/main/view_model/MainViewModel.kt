package com.orbits.testproject.main.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orbits.testproject.helper.WebServiceCall
import com.orbits.testproject.main.model.ExitPaymentJsonReturnStatus
import com.orbits.testproject.main.model.ExitPaymentResponseModel
import com.orbits.testproject.main.model.GetBarcodeRequestModel
import com.orbits.testproject.main.model.GetBarcodeResponseModel
import com.orbits.testproject.main.model.JsonReturnStatus
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.handleCoroutineException
import kotlinx.coroutines.launch
import kotlin.toString
import org.json.JSONObject



class MainViewModel : ViewModel(){
    val mutGetBarcodeResponse: MutableLiveData<GetBarcodeResponseModel?> = MutableLiveData()
    val mutExitPaymentDetailsResponse : MutableLiveData<ExitPaymentResponseModel?> = MutableLiveData()


    fun getBarcodeApi(
        model: GetBarcodeRequestModel
    ) {
        viewModelScope.launch( Dispatchers.IO + CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
            mutGetBarcodeResponse.postValue(
                GetBarcodeResponseModel(
                    jsonReturnStatus = JsonReturnStatus(
                    success =false,
                    errMessage = throwable.localizedMessage
                    )
                )
            )


        } ){
            try {
                val result= WebServiceCall().getBarcode(
                    "getBarcode",
                    Gate_Code = model.Gate_Code,
                    Error_Code = model.Error_Code?.toString(),
                    Error_Msg = model.Error_Msg,
                    Printer_Status = model.Printer_Status?.toString(),
                    Printer_Status_Str_En = model.Printer_Status_Str_En,
                    Printer_Status_Str_Ar = model.Printer_Status_Str_Ar
                )
                println("result : $result")
                val response = parseSoapResponse(result)
                println("response : $response")
                mutGetBarcodeResponse.postValue(response)
            } catch (e: Exception){
                mutGetBarcodeResponse.postValue(
                    GetBarcodeResponseModel(
                        jsonReturnStatus = JsonReturnStatus(
                            success =false,
                            errMessage = e.localizedMessage
                        )
                    )
                )
            }

        }
    }

    fun getPaymentDetailsApi(
        uid: String,
        uidType:Int,
        exitTime:String,
    ) {
        println("scanned uid $uid")
        viewModelScope.launch( Dispatchers.IO + CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
            mutExitPaymentDetailsResponse.postValue(
                ExitPaymentResponseModel(
                    jsonReturnStatus = ExitPaymentJsonReturnStatus(
                        success =false,
                        errMessage = throwable.localizedMessage
                    )
                )
            )

        }){
            try {
                val result= WebServiceCall().getPaymentDetails(
                    MethodName = "Get_Payment_Details",
                    UID = uid,
                    UID_Type = uidType,
                    Gate_Code = "E01",
                    Time_Of_Exit = exitTime,
                )
                println("result : $result")
                val response = parsePaymentDetailsSoapResponse(result)
                println("response : $response")
                mutExitPaymentDetailsResponse.postValue(response)

        }catch (e: Exception){
            mutExitPaymentDetailsResponse.postValue(
                ExitPaymentResponseModel(
                    jsonReturnStatus = ExitPaymentJsonReturnStatus(
                        success =false,
                        errMessage = e.localizedMessage
                    )
            )
            )
        }
        }
    }

    fun parseSoapResponse(response: String): GetBarcodeResponseModel {
        return try{
            val jsonObject = JSONObject(response)
            val barcode = jsonObject.optString("barcode",null)
            val serverTime = jsonObject.optString("serverTime",null)
            val jsonReturnStatusObject = jsonObject.getJSONObject("jsonReturnStatus")
            val success = jsonReturnStatusObject.optBoolean("success",false)
            val reason = jsonReturnStatusObject.optString("reason",null)
            val errMessage = jsonReturnStatusObject.optString("errMessage",null)

            GetBarcodeResponseModel(
                barcode = barcode,
                serverTime = serverTime,
                jsonReturnStatus = JsonReturnStatus(
                    success = success,
                    reason = reason,
                    errMessage = errMessage
                )
            )

        } catch (e: Exception) {
            // Handle parsing errors
            GetBarcodeResponseModel(
                jsonReturnStatus = JsonReturnStatus(
                    success = false,
                    errMessage = e.localizedMessage
                )
            )
        }
    }

    fun parsePaymentDetailsSoapResponse(response: String): ExitPaymentResponseModel {
        return try {

            val jsonObject= JSONObject(response)
            val remark = jsonObject.optString("Remark", null)
            val amountCharged = jsonObject.optString("amountCharged", null)
            val depositAvailable = jsonObject.optString("depositAvailable", null)
            val depositDeducted = jsonObject.optString("depositDeducted", null)
            val invoiceFiscal = jsonObject.optString("invoiceFiscal", null)
            val invoiceNo = jsonObject.optString("invoiceNo", null)
            val entryTime = jsonObject.optString("entryTime", null)
            val level = jsonObject.optString("level", null)
            val vatAmount = jsonObject.optString("vatAmount", null)
            val entryGate = jsonObject.optString("entryGate", null)
            val exitTime = jsonObject.optString("exitTime", null)
            val payTime = jsonObject.optString("payTime", null)

            val jsonReturnStatusObject = jsonObject.getJSONObject("jsonReturnStatus")
            val success = jsonReturnStatusObject.optBoolean("success", false)
            val reason = jsonReturnStatusObject.optString("reason", null)
            val errMessage = jsonReturnStatusObject.optString("errMessage", null)

            ExitPaymentResponseModel(
                Remark = remark,
                amountCharged = amountCharged,
                depositAvailable = depositAvailable,
                depositDeducted = depositDeducted,
                invoiceFiscal = invoiceFiscal,
                invoiceNo = invoiceNo,
                entryTime = entryTime,
                entryGate = entryGate,
                exitTime = exitTime,
                vatAmount = vatAmount,
                level = level,
                payTime = payTime,
                jsonReturnStatus = ExitPaymentJsonReturnStatus(
                    success = success,
                    reason = reason,
                    errMessage = errMessage
                )
            )


        }catch (e: Exception){
            ExitPaymentResponseModel(
                jsonReturnStatus = ExitPaymentJsonReturnStatus(
                    success = false,
                    errMessage = e.localizedMessage
                )
            )
        }

    }


}
