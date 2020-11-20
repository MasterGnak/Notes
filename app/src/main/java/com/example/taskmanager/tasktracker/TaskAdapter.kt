package com.example.taskmanager.tasktracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.database.Task
import com.example.taskmanager.databinding.ListItemTaskBinding


class TaskAdapter(private val clickListener: ClickListener) : ListAdapter<Task, TaskAdapter.ViewHolder>(TaskDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
        if (task.selected) {holder.itemView.setBackgroundColor(Color.CYAN)}
        else {holder.itemView.setBackgroundColor(Color.WHITE)}
        //holder.itemView.setOnClickListener{clickListener.onClick(task)}
        val name = holder.itemView.findViewById<TextView>(R.id.task_name)
        val deadline = holder.itemView.findViewById<TextView>(R.id.task_deadline)
        name.setOnLongClickListener{
            clickListener.onLongClick(task)
            if (task.selected) {holder.itemView.setBackgroundColor(Color.CYAN)}
            else {holder.itemView.setBackgroundColor(Color.WHITE)}
            true
        }
        deadline.setOnLongClickListener{
            clickListener.onLongClick(task)
            if (task.selected) {holder.itemView.setBackgroundColor(Color.CYAN)}
            else {holder.itemView.setBackgroundColor(Color.WHITE)}
            true
        }
        name.setOnClickListener{clickListener.onNameClicked(task)}
        deadline.setOnClickListener{clickListener.onDeadlineClicked(task)}
    }

    class ViewHolder private constructor(private val binding: ListItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.task = task
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    class ClickListener(val longClickListener: (task: Task) -> Boolean,
                        val clickListener: (task: Task) -> Unit,
                        val nameClickListener: (task: Task) -> Unit,
                        val deadlineClickListener: (task: Task) -> Unit) {
        fun onLongClick(task: Task) = longClickListener(task)
        fun onClick(task: Task) = clickListener(task)
        fun onNameClicked(task: Task) = nameClickListener(task)
        fun onDeadlineClicked(task: Task) = deadlineClickListener(task)
    }

    companion object TaskDiffCallback: DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }


    }
}

