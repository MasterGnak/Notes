package com.example.taskmanager.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDatabaseDao {

    @Insert
    fun insert(task: Task)

    @Insert
    fun insertTasks(tasks: List<Task>)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("SELECT * FROM task_table")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT COUNT(taskId) FROM task_table")
    fun getCount(): Int
}