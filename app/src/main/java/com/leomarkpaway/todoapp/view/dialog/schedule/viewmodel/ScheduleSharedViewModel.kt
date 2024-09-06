package com.leomarkpaway.todoapp.view.dialog.schedule.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ScheduleSharedViewModel : ViewModel() {

    private val _selectDateMillis = MutableLiveData(0L)
    val selectDateMillis: LiveData<Long> = _selectDateMillis

    private val _selectedReminder = MutableLiveData(0)
    val selectedReminder: LiveData<Int> = _selectedReminder

    private val _isReminderOn = MutableLiveData(false)
    val isReminderOn: LiveData<Boolean> = _isReminderOn

    fun updateIsReminderOn(boolean: Boolean) {
        viewModelScope.launch { _isReminderOn.value = boolean }
    }

    fun updateSelectReminder(int: Int) {
        viewModelScope.launch { _selectedReminder.value = int }
    }

    fun updateSelectDateMillis(dateMillis: Long) {
        viewModelScope.launch { _selectDateMillis.value = dateMillis }
    }

}