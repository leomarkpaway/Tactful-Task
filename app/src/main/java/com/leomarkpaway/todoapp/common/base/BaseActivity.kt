package com.leomarkpaway.todoapp.common.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<T : ViewModel, VB : ViewBinding> : AppCompatActivity() {
    abstract val viewModel: T
    abstract val layoutId: Int
    lateinit var binding: VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        initViews()
        subscribe()
    }

    abstract fun initViews()

    abstract fun subscribe()

    override fun onDestroy() {
        super.onDestroy()
    }

}