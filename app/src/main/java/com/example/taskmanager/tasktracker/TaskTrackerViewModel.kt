package com.example.taskmanager.tasktracker

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDatabaseDao
import kotlinx.coroutines.*

class TaskTrackerViewModel(val database: TaskDatabaseDao, application: Application): AndroidViewModel(application) {

    private val sortedTasks = database.getAllTasksDeadlineSorted()
    private val unsortedTasks = database.getAllTasks()
    val tasks = MediatorLiveData<List<Task>>()
    private var currentPrefs = "0"

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
        tasks.addSource(unsortedTasks){tasks.value = it}
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
        var task: Task?
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

    fun getLastTask(): Task {
        var task: Task
        runBlocking {
            task = getLast()
        }
        return task
    }

    private suspend fun getLast(): Task {
        var task: Task
        withContext(Dispatchers.IO) {
            task = database.getLastTask()
        }
        return task
    }

    fun updatePrefs(prefs: SharedPreferences) {
        val newPrefs = prefs.getString("sort", "0")
        if (newPrefs != currentPrefs) {
            if (newPrefs == "0") {
                tasks.removeSource(sortedTasks)
                tasks.addSource(unsortedTasks) {tasks.value = it}
            }
            else {
                tasks.removeSource(unsortedTasks)
                tasks.addSource(sortedTasks) {tasks.value = it}
            }
            currentPrefs = newPrefs!!
        }
    }
}