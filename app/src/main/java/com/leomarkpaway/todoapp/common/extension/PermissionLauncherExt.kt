package com.leomarkpaway.todoapp.common.extension

import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 * Extension function for AppCompatActivity to handle permission requests.
 *
 * Requires minimum SDK 23 (Android 6.0).
 *
 * @param permissionRequest The permission to request (e.g., Manifest.permission.POST_NOTIFICATIONS).
 * @param grantedCallback Optional callback invoked if the permission is granted.
 * @param deniedCallback Optional callback invoked if the permission is denied.
 *
 * Example usage:
 * ```
 * permissionLauncher(
 *     Manifest.permission.POST_NOTIFICATIONS,
 *     grantedCallback = {
 *         // Permission granted, proceed with your action
 *     },
 *     deniedCallback = {
 *         // Permission denied, handle accordingly
 *     }
 * )
 * ```
 */
fun AppCompatActivity.permissionLauncher(
    permissionRequest: String,
    grantedCallback: (() -> Unit)? = null,
    deniedCallback: (() -> Unit)? = null
) {
    this.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) grantedCallback?.invoke()
        else deniedCallback?.invoke()
    }.launch(permissionRequest)
}