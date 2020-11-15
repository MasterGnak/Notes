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

    private val _addButton = MutableLiveData<Boolean>()
    val addButtonClicked: LiveData<Boolean>
        get() = _addButton

    fun onAddButtonClicked() {
        _addButton.value = true
    }

    fun onAddButtonClickedEnded() {
        _addButton.value = false
    }

    fun addTask(taskName: String) {
        viewModelScope.launch {
            val newTask = Task(name = taskName)
            insert(newTask)
        }
    }

    suspend fun insert(task: Task) {
        withContext(Dispatchers.IO){
            database.insert(task)
        }
    }

    fun nuke() {
        viewModelScope.launch{ clear() }
    }

    suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }
}