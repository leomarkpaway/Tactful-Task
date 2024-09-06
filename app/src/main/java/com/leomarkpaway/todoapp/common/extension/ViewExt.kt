package com.leomarkpaway.todoapp.common.extension

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat

fun View.gone() { this.visibility = View.GONE }

fun View.visible() { this.visibility = View.VISIBLE }

fun View.invisible() { this.visibility = View.INVISIBLE }

fun View.startAnimation(context: Context, anim: Int) =
    this.startAnimation(AnimationUtils.loadAnimation(context, anim))

fun Context.showSoftKeyboard(view: EditText) {
    val imm = ContextCompat.getSystemService(this, InputMethodManager::class.java)
    imm?.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun Context.hideKeyboard(view: EditText) {
    view.clearFocus()
    val inputMethodManager = ContextCompat.getSystemService(
        this,
        InputMethodManager::class.java
    ) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.isKeyboardVisible(): Boolean {
    val imm = ContextCompat.getSystemService(
        this,
        InputMethodManager::class.java
    ) as InputMethodManager
    return imm.isAcceptingText
}
