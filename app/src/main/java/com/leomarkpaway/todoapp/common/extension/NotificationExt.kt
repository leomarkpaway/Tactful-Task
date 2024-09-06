package com.leomarkpaway.todoapp.common.extension

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.leomarkpaway.todoapp.R

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Context.checkNotificationPermission() = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

fun Context.createNotificationChannel(channelID: String, channelName: String) {
        val channel = NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val manager = getSystemService(this, NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
}

fun Context.createNotification(channelID: String, notificationId: Int,title: String, content: String) {
    val notification = NotificationCompat.Builder(this, channelID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setAutoCancel(true)
        .build()

    val manager = getSystemService(this, NotificationManager::class.java)
    manager?.notify(notificationId, notification)
}

fun Context.createNotification(channelID: String, notificationId: Int, title: String, content: String, openIntent: PendingIntent) {
    val notification = NotificationCompat.Builder(this, channelID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentIntent(openIntent)
        .setAutoCancel(true)
        .build()

    val manager = getSystemService(this, NotificationManager::class.java)
    manager?.notify(notificationId, notification)
}

fun Context.createNotificationWithAction(channelID: String, notificationId: Int, title: String, content: String, openIntent: PendingIntent, actionName: String, actionIntent: PendingIntent) {
    val notification = NotificationCompat.Builder(this, channelID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentIntent(openIntent)
        .addAction(0, actionName, actionIntent)
        .setAutoCancel(true)
        .build()

    val manager = getSystemService(this, NotificationManager::class.java)
    manager?.notify(notificationId, notification)
}