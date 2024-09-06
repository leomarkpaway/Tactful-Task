package com.leomarkpaway.todoapp.common.extension

import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

fun ImageView.setImage(context: Context, drawableID: Int) {
    this.setImageDrawable(ContextCompat.getDrawable(context, drawableID))
}

fun MaterialButton.setImage(context: Context, drawableID: Int) {
    this.icon = ContextCompat.getDrawable(context, drawableID)
}

fun ImageView.setRippleEffect() {
    val typedValue = TypedValue()
    this.context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, typedValue, true)
    val attribute = intArrayOf(android.R.attr.selectableItemBackgroundBorderless)
    val typedArray = this.context.obtainStyledAttributes(typedValue.resourceId, attribute)
    val backgroundDrawable = typedArray.getDrawable(0)
    typedArray.recycle()
    this.background = backgroundDrawable
}