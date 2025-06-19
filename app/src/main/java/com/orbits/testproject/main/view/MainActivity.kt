package com.orbits.testproject.main.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.orbits.testproject.R
import com.orbits.testproject.databinding.ActivityMainBinding
import com.orbits.testproject.databinding.LayoutPasswordDialogBinding
import com.orbits.testproject.helper.AlertDialogInterface
import com.orbits.testproject.helper.Constants
import com.orbits.testproject.main.model.GetBarcodeRequestModel
import com.orbits.testproject.main.view_model.MainViewModel
import com.orbits.testproject.settings.SettingsActivity
import kotlinx.coroutines.launch
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]



        setButtonListener()
        initObserver()



    }


    
    private fun setButtonListener(){
        binding.btnGenerate.setOnClickListener {
            viewModel.getBarcodeApi(
                GetBarcodeRequestModel(
                    Gate_Code = "E01",
                    Error_Code = 0,
                    Error_Msg = "Error",
                    Printer_Status = 1,
                    Printer_Status_Str_En = "",
                    Printer_Status_Str_Ar = ""
                )
            )
            
        }
        binding.btnNavigate.setOnClickListener {
            showPasswordDialog()

        }

    }



    private fun initObserver(){
        viewModel.mutGetBarcodeResponse.observe(this){
            lifecycleScope.launch {
                if (it!=null){
                    if (it.jsonReturnStatus?.success==true){
                        println("here is success ${it.barcode}")
                        binding.txtValue.text = it.barcode
                        viewModel.getPaymentDetailsApi(
                            uid = it.barcode ?: "",
                            uidType = 3,
                            exitTime = "2021-06-22 23:50:23.000"
                        )
                    } else {
                        println("here is failure")
                        binding.txtValue.text = it.jsonReturnStatus?.errMessage
                    }
                }
            }
        }

        viewModel.mutExitPaymentDetailsResponse.observe(this) {
            lifecycleScope.launch {
                if (it!=null){
                    if (it.jsonReturnStatus?.success==true){
                        binding.txtValue2.text = it.amountCharged
                    } else {
                        binding.txtValue2.text = it.jsonReturnStatus?.errMessage
                    }
                }
            }
        }
    }


    var customDialog: Dialog? = null

    fun showPasswordDialog(
        isCancellable: Boolean? = true,
    ) {
        try {
            customDialog = Dialog(this)
            customDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val binding: LayoutPasswordDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.layout_password_dialog, null, false
            )
            customDialog?.setContentView(binding.root)
            val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
            lp.copyFrom(customDialog?.window?.attributes)
            lp.width = getDimension(this as Activity, 300.00)
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.CENTER
            customDialog?.window?.attributes = lp
            customDialog?.setCanceledOnTouchOutside(isCancellable ?: true)
            customDialog?.setCancelable(isCancellable ?: true)


            binding.cancel.setOnClickListener {
                customDialog?.dismiss()
            }

            binding.btnConfirm.setOnClickListener {
                if (binding.edtPass.text.toString() == "12345"){
                    customDialog?.dismiss()
                    val intent = Intent(
                        this@MainActivity,SettingsActivity::class.java

                    )
                    startActivity(intent)
                } else {
                    Toast.makeText(this,"Invalid password", Toast.LENGTH_SHORT).show()
                }
            }
            customDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDimension(activity: Activity, size: Double): Int {
        return if (Constants.DEVICE_DENSITY > 0) {
            (Constants.DEVICE_DENSITY * size).toInt()
        } else {
            ((getDeviceWidthInDouble(activity) / 320) * size).toInt()

        }
    }


    fun getDeviceWidthInDouble(activity: Activity): Double {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels.toDouble()
    }
}