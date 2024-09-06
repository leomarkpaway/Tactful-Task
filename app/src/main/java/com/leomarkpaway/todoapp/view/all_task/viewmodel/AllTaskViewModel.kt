package com.leomarkpaway.todoapp.view.all_task.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leomarkpaway.todoapp.data.repository.Repository
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AllTaskViewModel(private val repository: Repository) : ViewModel() {

    private val _selectedTaskFilter = MutableLiveData(0)
    val selectedTaskFilter: LiveData<Int> = _selectedTaskFilter

    suspend fun getAllTask() = repository.getAllTask()

    fun updateSelectedTaskFilter(int: Int) {
        viewModelScope.launch {
            _selectedTaskFilter.value = int
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) { repository.deleteTask(task) }
    }

    fun updateTaskPin(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateTaskPin(task)
    }

}