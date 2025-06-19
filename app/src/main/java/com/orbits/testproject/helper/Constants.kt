package com.orbits.testproject.helper

import android.os.Build
import android.os.Environment
import com.orbits.testproject.BuildConfig
import java.io.File


object Constants {

    const val APP_PASSWORD = "1234"

    var DEVICE_TOKEN = ""
    val DEVICE_MODEL: String = Build.MODEL
    const val DEVICE_TYPE = "A" //passed in banners
    val OS_VERSION = Build.VERSION.RELEASE
    const val APP_VERSION = BuildConfig.VERSION_NAME




    const val DATE_FORMAT = "yyyy-MM-dd hh:mm:ss"


    var DEVICE_DENSITY = 0.0

    val fontBold = "bold"
    val fontRegular = "regular"
    val fontMedium = "medium"
    val fontRegularRev = "regular_reverse"


    const val TOOLBAR_ICON_ONE = "iconOne"
    const val TOOLBAR_ICON_TWO = "iconTwo"


    val configFile = File(
        Environment.getExternalStorageDirectory()
            .toString() + "/One_Tap_Pay/Config.xls"
    )

    val filePath = Environment.getExternalStorageDirectory()
        .toString() + "/One_Tap_Pay/Config.xls"



    /*Excel variables */

    const val MESSAGE_ONE = "Message One"
    const val MESSAGE_ONE_AR = "Message One Ar"
    const val MESSAGE_TWO = "Message Two"
    const val MESSAGE_TWO_AR = "Message Two Ar"
    const val MESSAGE_THREE = "Message Three"
    const val MESSAGE_THREE_AR = "Message Three Ar"
    const val MESSAGE_FOUR = "Message Four"
    const val MESSAGE_FOUR_AR = "Message Four Ar"

}
