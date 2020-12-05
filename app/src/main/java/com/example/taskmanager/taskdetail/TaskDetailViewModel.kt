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

    private val _editable = MutableLiveData<Boolean>()
    val editable: LiveData<Boolean>
        get() = _editable

    fun onDoneButtonClicked() {
        _doneButtonClicked.value = true
    }

    fun onDoneButtonFinish() {
        _doneButtonClicked.value = false
    }

    init{
        _editable.value = false
    }

    fun enableEditing() {
        _editable.value = true
    }

    fun disableEditing() {
        _editable.value = false
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

    fun parseDeadline(deadline: String): String {
        val date = dateFormat.parse(deadline)
        return if (date.before(calendar.time)) {
            throw Exception("Invalid date")
        }
        else dateFormat.format(date)
    }
}