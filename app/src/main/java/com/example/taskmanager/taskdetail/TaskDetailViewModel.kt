package com.example.taskmanager.taskdetail

import android.app.Application
import androidx.lifecycle.*
import com.example.taskmanager.calendar
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDatabase
import com.example.taskmanager.dateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.*


class TaskDetailViewModel(task: Task, application: Application) : AndroidViewModel(application) {

    val database = TaskDatabase.getInstance(application).taskDatabaseDao

    private val _selectedTask = MutableLiveData<Task>()
    val selectedTask: LiveData<Task>
        get() = _selectedTask

    init{
        _selectedTask.value = task
    }

    private val _doneButtonClicked = MutableLiveData<Boolean>()
    val doneButtonClicked: LiveData<Boolean>
        get() = _doneButtonClicked


    fun onDoneButtonClicked() {
        _doneButtonClicked.value = true
    }

    fun onDoneButtonFinish() {
        _doneButtonClicked.value = false
    }

    init{
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


}