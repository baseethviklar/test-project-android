package com.orbits.testproject.helper

import android.view.View

interface AlertDialogInterface {
    fun onYesClick() {}
    fun onMasterYesClick() {}
    fun onNoClick() {}
    fun onCloseDialog() {}
    fun onConfirmPort(port:String) {}
    fun onSubmitPasswordClick(password: String) {}
}