package com.leomarkpaway.tactfultask.view.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leomarkpaway.tactfultask.view.home.adapter.model.HomeFilterModel
import com.leomarkpaway.tactfultask.common.enum.TaskStatus.DONE
import com.leomarkpaway.tactfultask.common.enum.TaskStatus.EXPIRED
import com.leomarkpaway.tactfultask.common.enum.TaskStatus.PENDING
import com.leomarkpaway.tactfultask.data.repository.Repository
import com.leomarkpaway.tactfultask.data.source.local.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeSharedViewModel(private val repository: Repository) : ViewModel() {

    private val _isVisiblePinnedTask = MutableLiveData(true)
    val isVisiblePinnedTask: LiveData<Boolean> = _isVisiblePinnedTask

    private val _isVisiblePendingTask = MutableLiveData(true)
    val isVisiblePendingTask: LiveData<Boolean> = _isVisiblePendingTask

    private val _isVisibleCompletedTask = MutableLiveData(true)
    val isVisibleCompletedTask: LiveData<Boolean> = _isVisibleCompletedTask

    private val _searchValue = MutableLiveData("")
    val searchValue: LiveData<String> = _searchValue

    private val _isOpenFabMenu = MutableLiveData(false)
    val isOpenFabMenu: LiveData<Boolean> = _isOpenFabMenu

    private val _selectedFilter = MutableLiveData<HomeFilterModel>()
    val selectedFilter: LiveData<HomeFilterModel> = _selectedFilter

    private val _searchResult = MutableLiveData<List<Task>>()
    val searchResult: LiveData<List<Task>?> = _searchResult

    private val _currentTaskList = MutableLiveData<List<Task>>()
    val currentTaskList: LiveData<List<Task>?> = _currentTaskList

    private val _allTaskAdapterArrayList = MutableLiveData<ArrayList<Pair<Int,String>>>()
    val allTaskAdapterArrayList: LiveData<ArrayList<Pair<Int,String>>> = _allTaskAdapterArrayList

    suspend fun getWeeklyTasks() = repository.getAllTask()

    fun updateSearchResult(task: List<Task>) {
        viewModelScope.launch(Dispatchers.IO) { _searchResult.postValue(task) }
    }

    fun updateIsOpenFabMenu(boolean: Boolean) {
        viewModelScope.launch { _isOpenFabMenu.value = boolean }
    }

    fun updateIsVisiblePinnedTask(boolean: Boolean) {
        viewModelScope.launch(Dispatchers.Main) { _isVisiblePinnedTask.value = boolean }
    }

    fun updateIsVisiblePendingTask(boolean: Boolean) {
        viewModelScope.launch(Dispatchers.Main) { _isVisiblePendingTask.value = boolean }
    }

    fun updateIsVisibleCompletedTask(boolean: Boolean) {
        viewModelScope.launch(Dispatchers.Main) { _isVisibleCompletedTask.value = boolean }
    }

    fun updateSearchValue(string: String) {
        viewModelScope.launch { _searchValue.value = string }
    }

    fun updateCurrentTaskList(task: List<Task>) {
        viewModelScope.launch(Dispatchers.IO) { _currentTaskList.postValue(task) }
    }

    suspend fun getTasksByDate(date: String) = repository.getTasksByDate(date)
    suspend fun getAllTask() = repository.getAllTask()

    fun updateMarkAsDone(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            val taskCopy =
                if (task.status == PENDING.name || task.status == EXPIRED.name) task.copy(status = DONE.name)
                else task.copy(status = PENDING.name)
            repository.markAsDoneTask(taskCopy)
        }
    }

    fun updateTaskPin(task: Task) = viewModelScope.launch(Dispatchers.IO) { repository.updateTaskPin(task) }

    fun updateSelectedFilter(selectedFilter: HomeFilterModel) {
        viewModelScope.launch {
            _selectedFilter.value = selectedFilter
        }
    }

    fun updateAllTaskAdapterArrayList(arrayList : ArrayList<Pair<Int, String>>) =
        viewModelScope.launch {
            _allTaskAdapterArrayList.postValue(arrayList)
        }

}