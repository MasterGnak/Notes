package com.example.taskmanager.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskmanager.R
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    var taskId: Long = 0L,

    @ColumnInfo
    var name: String = "Empty task",

    @ColumnInfo
    var deadline: String = "New year",

    @ColumnInfo
    var detail: String = "Description",

    @ColumnInfo
    var priority: Int = 1,

    ): Parcelable