package com.leomarkpaway.tactfultask.view.dialog.create_update_delete_task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leomarkpaway.tactfultask.common.util.isTaskUpdatedValue
import com.leomarkpaway.tactfultask.data.repository.Repository
import com.leomarkpaway.tactfultask.data.source.local.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DialogSharedViewModel(private val repository: Repository): ViewModel() {

    private val _selectedTask = MutableLiveData<Task>()
    val selectedTask: LiveData<Task> = _selectedTask

    private val _isDateValid = MutableLiveData(true)
    val isDateValid: LiveData<Boolean> = _isDateValid

    private val _isPinned = MutableLiveData(false)
    val isPinned: LiveData<Boolean> = _isPinned

    fun addTodo(task: Task) {
        viewModelScope.launch(Dispatchers.IO) { repository.addTask(task) }
    }

    fun updateSelectedItem(task: Task) {
        viewModelScope.launch { _selectedTask.value = task }
    }

    fun updateTodo(newTask: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val oldTask = _selectedTask.value
            if (oldTask != null) {
                if (isTaskUpdatedValue(oldTask, newTask)) {
                    repository.updateTask(newTask)
                }
            }
        }
    }

    fun updateIsDateValid(boolean: Boolean) {
        viewModelScope.launch { _isDateValid.value = boolean }
    }

    fun deleteTodo() {
        viewModelScope.launch(Dispatchers.IO) {
            val selectedTodo = _selectedTask.value
            if (selectedTodo != null) {
                repository.deleteTask(selectedTodo)
            }
        }
    }

    fun updateTaskPin(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTaskPin(task)
    }

    fun updateIsPinned(boolean: Boolean) = viewModelScope.launch {
        _isPinned.postValue(boolean)
    }

}