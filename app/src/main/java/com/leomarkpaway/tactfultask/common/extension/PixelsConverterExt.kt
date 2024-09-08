package com.leomarkpaway.tactfultask.common.extension

import android.content.Context
import android.util.TypedValue

fun Int.convertPixelsToDp(context: Context): Float {
    val displayMetrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, this.toFloat(), displayMetrics)
}

fun Float.convertDpToPixels(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, displayMetrics).toInt()
}