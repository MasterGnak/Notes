package com.example.taskmanager.taskdetail

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.database.Task

class TaskDetailViewModelFactory(
    private val task: Task,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            return TaskDetailViewModel(task, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}