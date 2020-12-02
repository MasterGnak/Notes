package com.example.taskmanager.taskdetail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskmanager.database.Task

class TaskDetailViewModel(task: Task, application: Application) : ViewModel() {

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

//    private val _textViewVisible = MutableLiveData<Boolean>()
//    val textViewVisible: LiveData<Boolean>
//        get() = _textViewVisible


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

}