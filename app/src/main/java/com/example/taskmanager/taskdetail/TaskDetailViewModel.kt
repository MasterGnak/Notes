package com.example.taskmanager.taskdetail

import android.app.Application
import androidx.lifecycle.*
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDatabase
import kotlinx.coroutines.*


class TaskDetailViewModel(task: Task, application: Application) : AndroidViewModel(application) {

    val database = TaskDatabase.getInstance(application).taskDatabaseDao

    private val _selectedTask = MutableLiveData<Task>()
    val selectedTask: LiveData<Task>
        get() = _selectedTask

    init{
        _selectedTask.value = task
    }

    private val _dateClicked = MutableLiveData<Boolean>()
    val dateClicked: LiveData<Boolean>
        get() = _dateClicked


    fun onDateClicked() {
        _dateClicked.value = true
    }

    fun onDateClickedFinish() {
        _dateClicked.value = false
    }

    init{

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

    private suspend fun update(task:Task) {
        withContext(Dispatchers.IO){
            database.update(task)
        }
    }

}