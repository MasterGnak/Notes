package com.example.taskmanager.tasktracker

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.example.taskmanager.R
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDatabase
import com.example.taskmanager.databinding.FragmentTaskTrackerBinding
import kotlinx.android.synthetic.main.task_name_edit_text.view.*

class TaskTrackerFragment : Fragment() {

    private lateinit var viewModel: TaskTrackerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentTaskTrackerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_tracker, container, false)

        // Set up viewModel, that contains reference to data
        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = TaskTrackerViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TaskTrackerViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Set up a recycler view
        val adapter = TaskAdapter()
        binding.taskList.adapter = adapter
        val tracker = SelectionTracker.Builder<Long>(
            "taskSelection",
            binding.taskList,
            TaskAdapter.TaskItemKeyProvider(adapter),
            TaskAdapter.TaskDetailsLookup(binding.taskList),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(TaskAdapter.Predicate(viewModel)).build()
        tracker.addObserver(object: SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (!tracker.hasSelection()) viewModel.disableSelection()
            }
        })
        adapter.setSelectionTracker(tracker)
        adapter.setClickListener(TaskAdapter.ClickListener(
            //onLongClick
            {
                Log.i("Click listener", "Long click used")
                viewModel.enableSelection()
                tracker.select(it.taskId)
                true
            },

            //onNameClick
            {viewModel.onTaskNameClicked(it)},

            //onDeadlineClick
            {viewModel.onTaskDeadlineClicked(it)}

        ))

        viewModel.addButtonClicked.observe(viewLifecycleOwner, {
            if (it == true) {
                // Set up Alert Dialog
                val nameInput = inflater.inflate(R.layout.task_name_edit_text, null, false)
                val deadlineInput = EditText(context)
                deadlineInput.inputType = InputType.TYPE_CLASS_TEXT
                val dialogMain = AlertDialog.Builder(this.context!!).
                    setTitle("Enter task name").
                    setView(nameInput).
                    setPositiveButton("OK", null).
                    setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }.
                    create()
                dialogMain.setOnShowListener{dialog ->
                        dialogMain.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                            val taskName = nameInput.task_name_edit_text.text.toString()
                            (nameInput.parent as ViewGroup).removeView(nameInput)
                            dialog.cancel()
                            AlertDialog.Builder(this.context!!).setTitle("Enter deadline").setView(deadlineInput)
                                .setPositiveButton("OK") { dialog, _ ->
                                    dialog.cancel()
                                    viewModel.addTask(
                                        Task(
                                            name = taskName,
                                            deadline = deadlineInput.text.toString()
                                        )
                                    )
                                }.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }.create().show()
                        }
                }
                dialogMain.show()
                viewModel.onAddButtonClickedFinish()
            }
        })

        viewModel.taskNameClicked.observe(viewLifecycleOwner, {
            if (it != null) {
                val newNameInput = EditText(context)
                newNameInput.inputType = InputType.TYPE_CLASS_TEXT
                AlertDialog.Builder(this.context!!).
                setTitle("Enter task name").
                setView(newNameInput).
                setPositiveButton("OK") {dialog, _ ->
                    it.name = newNameInput.text.toString()
                    viewModel.updateTask(it)
                    dialog.dismiss()
                }.
                setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }.
                create().show()
                viewModel.onTaskNameClickedFinish()
            }
        })

        viewModel.taskDeadlineClicked.observe(viewLifecycleOwner, {
            if (it != null) {
                val newDeadlineInput = EditText(context)
                newDeadlineInput.inputType = InputType.TYPE_CLASS_TEXT
                AlertDialog.Builder(this.context!!).
                setTitle("Enter deadline").
                setView(newDeadlineInput).
                setPositiveButton("OK") {dialog, _ ->
                    it.deadline = newDeadlineInput.text.toString()
                    viewModel.updateTask(it)
                    dialog.dismiss()
                }.
                setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }.
                create().show()
                viewModel.onTaskDeadlineClickedFinish()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_task_tracker, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.add_task) {
            viewModel.onAddButtonClicked()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}