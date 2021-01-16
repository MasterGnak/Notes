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
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
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
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context)
        // Set up viewModel, that contains reference to data
        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = TaskTrackerViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TaskTrackerViewModel::class.java)
        viewModel.updatePrefs(sharedPreferences)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // Set up a recycler view
        val adapter = TaskAdapter(sharedPreferences)
        binding.taskList.adapter = adapter
        val tracker = SelectionTracker.Builder(
            "taskSelection",
            binding.taskList,
            TaskAdapter.TaskItemKeyProvider(adapter),
            TaskAdapter.TaskDetailsLookup(binding.taskList),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(TaskAdapter.Predicate(viewModel)).build()
        tracker.addObserver(object: SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                if (!tracker.hasSelection()) {
                    viewModel.disableSelection()
                    activity?.invalidateOptionsMenu()
                }
            }
        })
        adapter.setSelectionTracker(tracker)
        adapter.setClickListener(TaskAdapter.ClickListener(
            //onLongClick
            {
                viewModel.enableSelection()
                tracker.select(it.taskId)
                activity?.invalidateOptionsMenu()
                true
            },

            //onClick
            {
                if (!tracker.hasSelection()) viewModel.displayTaskDetails(it)
            }
        ))

        viewModel.addButtonClicked.observe(viewLifecycleOwner, {
            if (it == true) {
                // Set up Alert Dialog
                val nameInput = inflater.inflate(R.layout.task_name_edit_text, null, false)
                val deadlineInput = EditText(context)
                deadlineInput.inputType = InputType.TYPE_CLASS_DATETIME
                val dialogMain = AlertDialog.Builder(this.context!!).
                    setTitle("Enter task name").
                    setView(nameInput).
                    setPositiveButton("OK", null).
                    setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }.
                    create()
                dialogMain.setOnShowListener{dialog ->
                        dialogMain.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener{
                            viewModel.addTask(
                                Task(
                                    name = nameInput.task_name_edit_text.text.toString()
                                )
                            )
                            (nameInput.parent as ViewGroup).removeView(nameInput)
                            dialog.cancel()
                        }
                }
                dialogMain.show()
                viewModel.onAddButtonClickedFinish()
            }
        })

        viewModel.nukeClicked.observe(viewLifecycleOwner, { bool ->
            if (bool) {
                viewModel.nuke(tracker.selection.mapNotNull {
                    val task = viewModel.getTask(it)
                    task
                })
                tracker.clearSelection()
                viewModel.onNukeFinished()
            }
        })

        viewModel.navigateToSelectedTask.observe(viewLifecycleOwner, {
            if (it != null) {
                this.findNavController().navigate(TaskTrackerFragmentDirections.actionShowDetail(it))
                viewModel.displayTaskFinish()
            }
        })

        viewModel.settingsButtonClicked.observe(viewLifecycleOwner, {
            if (it == true) {
                this.findNavController().navigate(TaskTrackerFragmentDirections.actionShowSettings())
                viewModel.onSettingsButtonClickedFinish()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task_tracker, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete) {
            viewModel.onNukeClicked()
            return true
        }
        if (item.itemId == R.id.settings) {
            viewModel.onSettingsButtonClicked()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val settingsButton = menu.findItem(R.id.settings)
        val deleteButton = menu.findItem(R.id.delete)
        if (viewModel.selectionEnabled.value!!) {
            settingsButton.isEnabled = false
            settingsButton.isVisible = false
            deleteButton.isEnabled = true
            deleteButton.isVisible = true
        } else {
            settingsButton.isEnabled = true
            settingsButton.isVisible = true
            deleteButton.isEnabled = false
            deleteButton.isVisible = false
        }
    }

}