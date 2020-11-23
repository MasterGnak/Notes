package com.example.taskmanager

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.database.Task
import com.example.taskmanager.tasktracker.TaskAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Task>?) {
    val adapter = recyclerView.adapter as TaskAdapter
    adapter.submitList(data)
    Log.i("Binding adapter", "List submitted")
}

