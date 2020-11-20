package com.example.taskmanager.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    var taskId: Long = 0L,

    @ColumnInfo
    var name: String = "Empty task",

    @ColumnInfo
    var deadline: String = "New year",

    @ColumnInfo
    var priority: Int = 1,

    var selected: Boolean = false
    ) {
}