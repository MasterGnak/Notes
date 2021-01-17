package com.example.taskmanager

import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.database.Task
import com.example.taskmanager.tasktracker.TaskAdapter
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.ENGLISH)
val calendar: Calendar = Calendar.getInstance()
fun initializeFormat() {
    val beginningOfCentury = Calendar.getInstance()
    beginningOfCentury.set(2000,1,1)
    dateFormat.set2DigitYearStart(beginningOfCentury.time)
    dateFormat.isLenient = false
}


@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Task>?) {
    val adapter = recyclerView.adapter as TaskAdapter
    adapter.submitList(data)
}


@BindingAdapter("date")
fun setDateEditText(view: EditText, date: Long) {
    if (date == 0L) view.setText("")
    else view.setText(dateFormat.format(Date(date)))
}

@BindingAdapter("days_left")
fun setDaysRemaining(view: TextView, date: Long) {
    if (date == 0L) {
        view.text = ""
    } else {
        val diffInMillis = date - calendar.time.time
        val diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
        if (diff >= 0) view.text = "$diff д"
        else view.text = "RIP"
    }
}

fun checkDaysRemaining(date: Long): Long {
    val diffInMillis = date - calendar.time.time
    return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
}

