package com.leomarkpaway.tactfultask.common.util

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.leomarkpaway.tactfultask.common.extension.showToastLong

fun checkSinglePermissionAny(
    activity: Activity,
    permissionName : String,
    permissionCode : Int
): Boolean {
    if (ContextCompat.checkSelfPermission(
            activity,
            permissionName
        ) == PackageManager.PERMISSION_DENIED){
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(permissionName),
            permissionCode
        )
    }else{
        return true
    }
    return false
}

fun appSettingOpen(context: Context){
    context.showToastLong("Go to Settings and Enable Permission")
    val settingIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    settingIntent.data = Uri.parse("package:${context.packageName}")
    context.startActivity(settingIntent)
}

fun warningPermissionDialog(context: Context,listener: DialogInterface.OnClickListener){
    MaterialAlertDialogBuilder(context)
        .setMessage("Permission are required for this app")
        .setCancelable(false)
        .setPositiveButton("Ok",listener)
        .create()
        .show()
}