package com.leomarkpaway.tactfultask.receiver.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leomarkpaway.tactfultask.common.enum.TaskStatus
import com.leomarkpaway.tactfultask.common.util.isTaskUpdatedValue
import com.leomarkpaway.tactfultask.data.repository.Repository
import com.leomarkpaway.tactfultask.data.source.local.entity.Notification
import com.leomarkpaway.tactfultask.data.source.local.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ReceiverViewModel(private val repository: Repository) : ViewModel() {

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    fun saveNotification(notification: Notification) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveNotification(notification)
        }
    }

    suspend fun getTaskById(id: Long) = repository.getTask(id)

    suspend fun getNotifications() = repository.getNotification()

    fun clearNotifications() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearNotification()
        }
    }

    fun setTaskExpired(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedTask = getTaskById(id)
            val updateTask = selectedTask?.copy(status = TaskStatus.EXPIRED.name)
            if (selectedTask != null && isTaskUpdatedValue(selectedTask, updateTask!!)) {
                repository.setTaskExpired(updateTask)
            }
        }
    }

}