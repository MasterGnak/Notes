package com.example.taskmanager.taskdetail

import android.app.Application
import android.widget.EditText
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

    private val _textViewClicked = MutableLiveData<Int?>()
    val textViewClicked: LiveData<Int?>
        get() = _textViewClicked

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
        _textViewClicked.value = null
    }

    fun enableEditing(viewId: Int?) {
        _textViewClicked.value = viewId
    }

    fun disableEditing() {
        _textViewClicked.value = null
    }

}