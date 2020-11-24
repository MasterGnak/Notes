package com.example.taskmanager.tasktracker

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.size
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.database.Task
import com.example.taskmanager.databinding.ListItemTaskBinding


class TaskAdapter() : ListAdapter<Task, TaskAdapter.ViewHolder>(TaskDiffCallback) {

    private lateinit var clickListener: ClickListener

    fun setClickListener(clickL: ClickListener) {
        clickListener = clickL
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).taskId
    }

    private var selectionTracker: SelectionTracker<Long>? = null

    fun setSelectionTracker(selectionTracker: SelectionTracker<Long>) {
        this.selectionTracker = selectionTracker
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task, selectionTracker!!.isSelected(task.taskId))
        val name = holder.itemView.findViewById<TextView>(R.id.task_name)
        val deadline = holder.itemView.findViewById<TextView>(R.id.task_deadline)

        holder.itemView.setOnLongClickListener{clickListener.onLongClick(task)}

////        name.setOnLongClickListener{
////            clickListener.onLongClick(task)
////        }
////
////        deadline.setOnLongClickListener{
////            clickListener.onLongClick(task)
////        }
//
//        name.setOnClickListener{clickListener.onNameClicked(task)}
//
//        deadline.setOnClickListener{clickListener.onDeadlineClicked(task)}
    }

    class ViewHolder private constructor(private val binding: ListItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
        var details: Details = Details()

        fun bind(task: Task, isActivated: Boolean) {
            binding.task = task
            details.key = task.taskId
            itemView.isActivated = isActivated
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                return ViewHolder(ListItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> {
            details.pos = layoutPosition
            return details
        }
    }

    class ClickListener(val longClickListener: (task: Task) -> Boolean,
                        val nameClickListener: (task: Task) -> Unit,
                        val deadlineClickListener: (task: Task) -> Unit) {
        fun onLongClick(task: Task) = longClickListener(task)
        fun onNameClicked(task: Task) = nameClickListener(task)
        fun onDeadlineClicked(task: Task) = deadlineClickListener(task)
    }

    companion object TaskDiffCallback: DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            Log.i("Task adapter", "Comparing contents the same ${oldItem.name} and ${newItem.name}, result is ${oldItem == newItem}")
            return oldItem == newItem
        }
    }

    class Details: ItemDetailsLookup.ItemDetails<Long>() {
        var pos: Int = -1
        var key: Long = -1L

        override fun getPosition(): Int {
            return pos
        }

        override fun getSelectionKey(): Long? {
            return key
        }

        override fun inSelectionHotspot(e: MotionEvent): Boolean {
            return true
        }
    }

    class TaskItemKeyProvider(private val adapter: TaskAdapter):
        ItemKeyProvider<Long>(SCOPE_CACHED) {
        override fun getKey(position: Int): Long {
            Log.i("getKey", "Getting key at pos $position, key = ${adapter.getItemId(position)}")
            return adapter.getItemId(position)
        }

        override fun getPosition(key: Long): Int {

            var i = 0
            while(true) {
                if (adapter.getItemId(i) == key) {
                    Log.i("getPosition", "Getting position of key $key, position = $i")
                    return i
                }
                i++
            }
        }
    }

    class TaskDetailsLookup(private val recyclerView: RecyclerView): ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)
            if (view != null) {
                val viewHolder = recyclerView.getChildViewHolder(view) as ViewHolder
                return viewHolder.getItemDetails()
            }
            return null
        }
    }

    class Predicate(private val viewModel: TaskTrackerViewModel): SelectionTracker.SelectionPredicate<Long>() {
        override fun canSetStateForKey(key: Long, nextState: Boolean): Boolean {
            return viewModel.selectionEnabled.value!!
        }

        override fun canSetStateAtPosition(position: Int, nextState: Boolean): Boolean {
            return viewModel.selectionEnabled.value!!
        }

        override fun canSelectMultiple(): Boolean {
            return true
        }
    }
}

