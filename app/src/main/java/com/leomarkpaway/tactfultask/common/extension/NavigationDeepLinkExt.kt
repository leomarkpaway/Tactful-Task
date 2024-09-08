package com.leomarkpaway.tactfultask.common.extension

import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.navigation.NavDeepLinkBuilder

fun Context.createPendingDeepLink(navHostId: Int, destinationId: Int, args: Bundle): PendingIntent {
    return NavDeepLinkBuilder(this)
        .setGraph(navHostId)
        .setDestination(destinationId)
        .setArguments(args)
        .createPendingIntent()
}
