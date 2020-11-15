package com.example.taskmanager.tasktracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskTrackerViewModel(val database: TaskDatabaseDao, application: Application): AndroidViewModel(application) {
    val tasks = database.getAllTasks()

    fun addTask() {
        val newTask = Task()
        viewModelScope.launch { insert(newTask) }
    }

    suspend fun insert(task: Task) {
        withContext(Dispatchers.IO){
            database.insert(task)
        }
    }
}