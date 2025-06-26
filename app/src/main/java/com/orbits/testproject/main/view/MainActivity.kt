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
import android.util.Log
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

        try {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
            viewModel = ViewModelProvider(this)[MainViewModel::class.java]

            // Check startup reason with error handling
            checkStartupReason()

            setButtonListener()
            initObserver()

            Log.d("MainActivity", "MainActivity created successfully")

        } catch (e: Exception) {
            Log.e("MainActivity", "Error in onCreate", e)
            // Don't crash on boot startup
            finish()
        }
    }


    /**
     * Check how the app was started and show appropriate message
     */
    private fun checkStartupReason() {
        try {
            val autoStarted = intent.getBooleanExtra("AUTO_STARTED", false)
            val crashRestart = intent.getBooleanExtra("CRASH_RESTART", false)

            Log.d("MainActivity", "Checking startup reason - autoStarted: $autoStarted, crashRestart: $crashRestart")

            when {
                autoStarted -> {
                    Log.d("MainActivity", "App auto-started after device boot")
                    Toast.makeText(this, "App started automatically after boot", Toast.LENGTH_LONG).show()
                    binding.txtValue.text = "Auto-started after boot"
                }
                crashRestart -> {
                    Log.d("MainActivity", "App restarted after crash recovery")
                    Toast.makeText(this, "App recovered from crash", Toast.LENGTH_LONG).show()
                    binding.txtValue.text = "Recovered from crash"
                }
                else -> {
                    Log.d("MainActivity", "App started normally by user")
                    binding.txtValue.text = "Ready to generate barcode"
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in checkStartupReason", e)
        }
    }


    /**
     * Test method to simulate app crash for testing crash handler
     */
    private fun testCrashHandler() {
        Log.d("MainActivity", "Testing crash handler...")
        Toast.makeText(this, "Testing crash handler in 2 seconds...", Toast.LENGTH_SHORT).show()

        // Delay crash to show toast message first
        binding.txtValue.postDelayed({
            throw RuntimeException("Test crash for crash handler verification")
        }, 2000)
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

        // Long press on generate button to test crash handler
        binding.btnGenerate.setOnLongClickListener {
            testCrashHandler()
            true
        }
    }

    private fun initObserver(){
        viewModel.mutGetBarcodeResponse.observe(this){
            lifecycleScope.launch {
                if (it!=null){
                    if (it.jsonReturnStatus?.success==true){
                        println("here is success ${it.barcode}")
                        Log.d("MainActivity", "Barcode generated successfully: ${it.barcode}")
                        binding.txtValue.text = it.barcode
                        viewModel.getPaymentDetailsApi(
                            uid = it.barcode ?: "",
                            uidType = 3,
                            exitTime = "2021-06-22 23:50:23.000"
                        )
                    } else {
                        println("here is failure")
                        Log.e("MainActivity", "Barcode generation failed: ${it.jsonReturnStatus?.errMessage}")
                        binding.txtValue.text = it.jsonReturnStatus?.errMessage
                    }
                }
            }
        }

        viewModel.mutExitPaymentDetailsResponse.observe(this) {
            lifecycleScope.launch {
                if (it!=null){
                    if (it.jsonReturnStatus?.success==true){
                        Log.d("MainActivity", "Payment details retrieved: ${it.amountCharged}")
                        binding.txtValue2.text = it.amountCharged
                    } else {
                        Log.e("MainActivity", "Payment details failed: ${it.jsonReturnStatus?.errMessage}")
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
                    Log.d("MainActivity", "Password correct - navigating to settings")
                    val intent = Intent(
                        this@MainActivity, SettingsActivity::class.java
                    )
                    startActivity(intent)
                } else {
                    Log.w("MainActivity", "Invalid password entered")
                    Toast.makeText(this,"Invalid password", Toast.LENGTH_SHORT).show()
                }
            }
            customDialog?.show()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error showing password dialog", e)
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "MainActivity destroyed")
    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity", "MainActivity paused")
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "MainActivity resumed")
    }
}
