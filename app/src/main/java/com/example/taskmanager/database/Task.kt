package com.example.taskmanager.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "task_table")
data class Task(

    @PrimaryKey(autoGenerate = true)
    var taskId: Long = 0L,

    @ColumnInfo
    var name: String = "",

    @ColumnInfo
    var deadline: String = "",

    @ColumnInfo
    var detail: String = "",

    @ColumnInfo
    var priority: Int = 1,

    @ColumnInfo
    var day: Int = 0,

    @ColumnInfo
    var month: Int = 0,

    @ColumnInfo
    var year: Int = 3000

//    @ColumnInfo
//    var date: Date? = null

): Parcelable