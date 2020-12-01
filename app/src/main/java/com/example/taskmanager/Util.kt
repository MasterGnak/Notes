package com.example.taskmanager

import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.database.Task
import com.example.taskmanager.tasktracker.TaskAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Task>?) {
    val adapter = recyclerView.adapter as TaskAdapter
    adapter.submitList(data)
}

@BindingAdapter("visible")
fun visibilityTextCheck(view: TextView, visible: Int?) {
    if (visible == null) view.visibility = View.VISIBLE
    else view.visibility = View.GONE
}

@BindingAdapter("visible")
fun visibilityEditCheck(view: EditText, visible: Int?) {
    if (visible != null) view.visibility = View.VISIBLE
    else view.visibility = View.GONE
}

@BindingAdapter("visible")
fun visibilityEditCheck(view: ScrollView, visible: Boolean) {
    if (visible) view.visibility = View.VISIBLE
    else view.visibility = View.GONE
}

