package com.leomarkpaway.tactfultask.common.extension

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Context.showToastLong(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showToastLong(@StringRes resId: Int) {
    Toast.makeText(this, resId, Toast.LENGTH_LONG).show()
}

fun Fragment.showToastShort(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToastShort(@StringRes resId: Int) {
    Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToastLong(message: String) {
    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
}

fun Fragment.showToastLong(@StringRes resId: Int) {
    Toast.makeText(requireContext(), resId, Toast.LENGTH_LONG).show()
}

fun Context.createToastShort(message: String): Toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)

fun Context.createToastShort(@StringRes resId: Int): Toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT)

fun Toast.toastCallBack(onShown: () -> Unit, onHide: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        addCallback((object : Toast.Callback() {
            override fun onToastShown() {
                super.onToastShown()
                onShown()
            }

            override fun onToastHidden() {
                super.onToastHidden()
                onHide()
            }
        }))
    }
}