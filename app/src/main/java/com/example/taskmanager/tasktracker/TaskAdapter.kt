package com.example.taskmanager.tasktracker


import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.checkDaysRemaining
import com.example.taskmanager.database.Task
import com.example.taskmanager.databinding.ListItemTaskBinding


class TaskAdapter(prefs: SharedPreferences) : ListAdapter<Task, TaskAdapter.ViewHolder>(TaskDiffCallback) {

    private val redZone = prefs.getString("red","2")!!.toLong()
    private val yellowZone = prefs.getString("yellow", "2")!!.toLong()

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
        holder.itemView.setOnLongClickListener{clickListener.onLongClick(task)}
        holder.itemView.setOnClickListener{clickListener.onClick(task)}
        if (task.deadline.isNotBlank()) {
            when (checkDaysRemaining(task.deadline)) {
                in -10000L..redZone -> holder.itemView.setBackgroundResource(R.drawable.item_background_round_red)
                in (redZone+1L)..(redZone+1L+yellowZone) -> holder.itemView.setBackgroundResource(R.drawable.item_background_round_yellow)
                else -> holder.itemView.setBackgroundResource(R.drawable.item_background_round_white)
            }
        }
    }

    class ViewHolder private constructor(private val binding: ListItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
        private var details: Details = Details()

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

    class ClickListener(val onLongClickListener: (task: Task) -> Boolean,
                        val onClickListener: (task: Task) -> Unit, ) {
        fun onLongClick(task: Task) = onLongClickListener(task)
        fun onClick(task: Task) = onClickListener(task)
    }

    companion object TaskDiffCallback: DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.taskId == newItem.taskId
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    class Details: ItemDetailsLookup.ItemDetails<Long>() {
        var pos: Int = -1
        var key: Long = -1L

        override fun getPosition(): Int {
            return pos
        }

        override fun getSelectionKey(): Long {
            return key
        }

        override fun inSelectionHotspot(e: MotionEvent): Boolean {
            return true
        }
    }

    class TaskItemKeyProvider(private val adapter: TaskAdapter):
        ItemKeyProvider<Long>(SCOPE_CACHED) {
        override fun getKey(position: Int): Long {
            return adapter.getItemId(position)
        }

        override fun getPosition(key: Long): Int {
            var i = 0
            while(true) {
                if (adapter.getItemId(i) == key) {
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

