package com.leomarkpaway.todoapp.common.extension

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Activity.checkPermission(manifestRequest: String) =
    ContextCompat.checkSelfPermission(this, manifestRequest) != PackageManager.PERMISSION_GRANTED

fun Activity.requestPermission(manifestRequest: String, requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(manifestRequest), requestCode)
}
