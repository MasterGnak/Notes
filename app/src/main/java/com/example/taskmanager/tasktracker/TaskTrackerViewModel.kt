package com.example.taskmanager.tasktracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskTrackerViewModel(val database: TaskDatabaseDao, application: Application): AndroidViewModel(application) {
    val tasks = database.getAllTasks()

    val selectedTasks = mutableListOf<Task>()
    val anySelected: Boolean
        get() = selectedTasks.isNotEmpty()

    private val _addButtonClicked = MutableLiveData<Boolean>()
    val addButtonClicked: LiveData<Boolean>
        get() = _addButtonClicked

    private val _taskNameClicked = MutableLiveData<Task?>()
    val taskNameClicked: LiveData<Task?>
        get() = _taskNameClicked

    private val _taskDeadlineClicked = MutableLiveData<Task?>()
    val taskDeadlineClicked: LiveData<Task?>
        get() = _taskDeadlineClicked

    fun onAddButtonClicked() {
        _addButtonClicked.value = true
    }

    fun onAddButtonClickedFinish() {
        _addButtonClicked.value = false
    }

    fun onTaskNameClicked(task: Task) {
        _taskNameClicked.value = task
    }

    fun onTaskNameClickedFinish() {
        _taskNameClicked.value = null
    }

    fun onTaskDeadlineClicked(task: Task) {
        _taskDeadlineClicked.value = task
    }

    fun onTaskDeadlineClickedFinish() {
        _taskDeadlineClicked.value = null
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            insert(task)
        }
    }

    private suspend fun insert(task: Task) {
        withContext(Dispatchers.IO){
            database.insert(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            update(task)
        }
    }

    private suspend fun update(task: Task) {
        withContext(Dispatchers.IO) {
            database.update(task)
        }
    }

    fun nuke() {
        viewModelScope.launch{ clear() }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.deleteTasks(selectedTasks)
            selectedTasks.clear()
        }
    }
}