package com.example.taskmanager

import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.database.Task
import com.example.taskmanager.tasktracker.TaskAdapter
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

@BindingAdapter("visible")
fun visibilityTextCheck(view: TextView, editable: Boolean) {
    if (!editable) view.visibility = View.VISIBLE
    else view.visibility = View.GONE
}

@BindingAdapter("visible")
fun visibilityEditCheck(view: EditText, editable: Boolean) {
    if (editable) view.visibility = View.VISIBLE
    else view.visibility = View.GONE
}

@BindingAdapter("visible")
fun visibilityEditCheck(view: ScrollView, editable: Boolean) {
    if (editable) view.visibility = View.VISIBLE
    else view.visibility = View.GONE
}

@BindingAdapter("date")
fun setDate(view: TextView, date: String) {
    view.text = date
}

@BindingAdapter("date")
fun setDateEditText(view: EditText, date: String) {
    view.setText(date)
}

@BindingAdapter("days_left")
fun setDaysRemaining(view: TextView, date: String) {
    if (date.isEmpty()) {
        view.text = ""
    } else {
        val diffInMillis = dateFormat.parse(date).time - calendar.time.time
        val diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
        if (diff >= 0) view.text = "$diff дней"
        else view.text = "Просрочено"
    }
}

fun checkDaysRemaining(date: String): Long {
    val diffInMillis = abs(dateFormat.parse(date).time - calendar.time.time)
    return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS)
}



