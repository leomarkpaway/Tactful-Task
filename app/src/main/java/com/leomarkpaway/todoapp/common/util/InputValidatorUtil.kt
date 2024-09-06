package com.leomarkpaway.todoapp.common.util

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class InputValidatorUtil(private val helperText: String, private val inputLayout: TextInputLayout) :
    TextWatcher {

    private lateinit var editText: TextInputEditText

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        inputLayout.helperText = if (s.isNullOrEmpty()) helperText else null
    }

    override fun afterTextChanged(s: Editable?) {
        inputLayout.helperText = if (s.isNullOrEmpty()) helperText else null
    }

    fun setListener(editText: TextInputEditText) {
        this.editText = editText
        this.editText.addTextChangedListener(this)
    }

    fun isValid() = !this.editText.text.isNullOrEmpty()

    fun getStringValue() = this.editText.text.toString()
}