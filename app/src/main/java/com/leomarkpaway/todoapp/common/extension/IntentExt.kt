package com.leomarkpaway.todoapp.common.extension

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

inline fun <reified T : Activity> Context.intentActivity(): Intent = Intent(this, T::class.java)
inline fun <reified T : Activity> Context.pendingIntentActivity(
    requestCode: Int,
    flag: Int,
    apply: (Intent) -> Unit
): PendingIntent {
    val intentActivity = Intent(this, T::class.java).apply(apply)
    return PendingIntent.getActivity(this, requestCode, intentActivity, flag)
}
inline fun <reified T : BroadcastReceiver> Context.pendingIntentReceiver(
    requestCode: Int,
    flag: Int,
    apply: (Intent) -> Unit
): PendingIntent {
    val intentReceiver = Intent(this, T::class.java).apply(apply)
    return PendingIntent.getBroadcast(this, requestCode, intentReceiver, flag)
}