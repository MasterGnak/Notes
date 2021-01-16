package com.example.taskmanager.tasktracker

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class TaskTrackerViewModel(val database: TaskDatabaseDao, application: Application): AndroidViewModel(application) {
    val tasks = database.getAllTasks()

    private val _addButtonClicked = MutableLiveData<Boolean>()
    val addButtonClicked: LiveData<Boolean>
        get() = _addButtonClicked

    private val _selectionEnabled = MutableLiveData<Boolean>()
    val selectionEnabled: LiveData<Boolean>
        get() = _selectionEnabled

    private val _navigateToSelectedTask = MutableLiveData<Task?>()
    val navigateToSelectedTask: LiveData<Task?>
        get() = _navigateToSelectedTask

    private val _settingsButtonClicked = MutableLiveData<Boolean>()
    val settingsButtonClicked: LiveData<Boolean>
        get() = _settingsButtonClicked

    init{
        _selectionEnabled.value = false
    }

    fun onAddButtonClicked() {
        _addButtonClicked.value = true
    }

    fun onAddButtonClickedFinish() {
        _addButtonClicked.value = false
    }

    fun onSettingsButtonClicked() {
        _settingsButtonClicked.value = true
    }

    fun onSettingsButtonClickedFinish() {
        _settingsButtonClicked.value = false
    }

    fun enableSelection() {
        _selectionEnabled.value = true
    }

    fun disableSelection() {
        _selectionEnabled.value = false
    }

    fun displayTaskDetails(task: Task) {
        _navigateToSelectedTask.value = task
    }

    fun displayTaskFinish() {
        _navigateToSelectedTask.value = null
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            insert(task)
        }
    }

    private suspend fun insert(task: Task) {
        withContext(Dispatchers.IO){
            database.insert(task)
            Log.i("viewModel", "inserted")
        }
    }

    private val _nukeClicked = MutableLiveData<Boolean>()
    val nukeClicked: LiveData<Boolean>
        get() = _nukeClicked

    fun onNukeClicked() {
        _nukeClicked.value = true
    }

    fun onNukeFinished() {
        _nukeClicked.value = false
    }

    fun nuke(selected: List<Task>) {
        viewModelScope.launch{ clear(selected) }
    }

    private suspend fun clear(selected: List<Task>) {
        withContext(Dispatchers.IO) {
            database.deleteTasks(selected)
        }
    }

    fun getTask(key: Long): Task? {
        var task: Task? = null
        runBlocking{
            task = get(key)
        }
        return task
    }

    private suspend fun get(key: Long): Task? {
        var task: Task?
        withContext(Dispatchers.IO) {
            task =  database.getTask(key)
        }
        return task
    }


}