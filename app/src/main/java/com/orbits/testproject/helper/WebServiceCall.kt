package com.orbits.testproject.helper

import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE

class WebServiceCall {
    var namespace: String = "http://tempuri.org/"
    var SOAP_ACTION: String? = null
    var request: SoapObject? = null
    var envelope: SoapSerializationEnvelope? = null
    var androidHttpTransport: HttpTransportSE? = null

    private fun setEnvelope() {
        try {
            val url = "http://74.208.175.118:8080/carparkingws.asmx"
            envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
            envelope?.dotNet = true
            envelope?.setOutputSoapObject(request)
            androidHttpTransport = HttpTransportSE(url)
            // androidHttpTransport.debug = true;
        } catch (e: Exception) {
            println("Soap Exception---->>>$e")
        }
    }

    fun getBarcode(
        MethodName: String,
        Gate_Code: String?,
        Error_Code: String?,
        Error_Msg: String?,
        Printer_Status: String?,
        Printer_Status_Str_En: String?,
        Printer_Status_Str_Ar: String?
    ): String {
        try {
            SOAP_ACTION = namespace + MethodName
            request = SoapObject(namespace, MethodName)
            request?.addProperty("Gate_Code", Gate_Code)
            request?.addProperty("Error_Code", Error_Code)
            request?.addProperty("Error_Msg", Error_Msg)
            request?.addProperty("Printer_Status", Printer_Status)
            request?.addProperty("Printer_Status_Str_En", Printer_Status_Str_En)
            request?.addProperty("Printer_Status_Str_Ar", Printer_Status_Str_Ar)
            println("SOAP_ACTION :: $SOAP_ACTION")
            println("request :: $request")
            setEnvelope()
            try {
                androidHttpTransport?.call(SOAP_ACTION, envelope)
                val result: String = envelope?.response.toString()
                println("result :: ${result}")
                return result
            } catch (e: Exception) {
                // TODO: handle exception
                return e.toString()
            }
        } catch (e: Exception) {
            // TODO: handle exception
            return e.toString()
        }
    }




    fun getPaymentDetails(
        MethodName: String,
        UID: String?,
        UID_Type: Int?,
        Gate_Code: String?,
        Time_Of_Exit: String?,
    ): String {
        try {
            SOAP_ACTION = namespace + MethodName
            request = SoapObject(namespace, MethodName)
            request?.addProperty("UID", UID)
            request?.addProperty("UID_Type", UID_Type)
            request?.addProperty("Gate_Code", Gate_Code)
            request?.addProperty("Time_Of_Exit", Time_Of_Exit)
            println("SOAP_ACTION :: $SOAP_ACTION")
            println("request :: $request")
            setEnvelope()
            try {
                androidHttpTransport?.call(SOAP_ACTION, envelope)
                val result: String = envelope?.response.toString()
                println("result :: ${result}")
                return result
            } catch (e: Exception) {
                return e.toString()
            }
        } catch (e: Exception) {
            return e.toString()
        }
    }

}