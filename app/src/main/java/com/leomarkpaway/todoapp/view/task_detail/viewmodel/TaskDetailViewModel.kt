package com.leomarkpaway.todoapp.view.task_detail.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leomarkpaway.todoapp.common.util.isTaskUpdatedValue
import com.leomarkpaway.todoapp.data.repository.Repository
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class TaskDetailViewModel(private val repository: Repository) : ViewModel() {

    private val _currentTaskDetails = MutableLiveData<Task>()
    val currentTaskDetails: LiveData<Task> = _currentTaskDetails

    private val _isTaskDone = MutableLiveData(false)
    val isTaskDone: LiveData<Boolean> = _isTaskDone

    private val _isPinned = MutableLiveData(false)
    val isPinned: LiveData<Boolean> = _isPinned

    fun getTaskById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val task = repository.getTask(id)
            task.let { _currentTaskDetails.postValue(it) }
        }
    }

    fun updateCurrentTaskDetails(task: Task) {
        viewModelScope.launch {
            _currentTaskDetails.value = task
        }
    }

    fun updateIsTaskDone(isTaskDone: Boolean) {
        viewModelScope.launch {
            _isTaskDone.value = isTaskDone
        }
    }

    fun updateIsPinned(boolean: Boolean) = viewModelScope.launch {
        _isPinned.postValue(boolean)
    }

    fun submitTaskDetailsChanges(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val oldTask = _currentTaskDetails.value
            Timber.d("submitTaskDetailsChanges1 $task")
            if (oldTask != null && isTaskUpdatedValue(oldTask, task)) {
                Timber.d("submitTaskDetailsChanges $task")
                repository.updateTask(task)
            }
        }
    }
}