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
    fun deleteTasks(tasks: List<Task>)

    @Query("DELETE FROM task_table")
    fun clear()

    @Query("SELECT * FROM task_table WHERE taskId = :key")
    fun getTask(key: Long): Task?

    @Query("SELECT * FROM task_table ORDER BY taskId DESC")
    fun getAllTasks(): LiveData<List<Task>>

    @Query("SELECT COUNT(taskId) FROM task_table")
    fun getCount(): Int
}