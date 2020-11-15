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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.database.TaskDatabase
import com.example.taskmanager.databinding.FragmentTaskTrackerBinding

class TaskTrackerFragment : Fragment() {

    private lateinit var viewModel: TaskTrackerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentTaskTrackerBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_task_tracker, container, false)

        // Set up a recycler view
        val adapter = TaskAdapter()
        val manager = LinearLayoutManager(activity)
        binding.taskList.adapter = adapter
        binding.taskList.layoutManager = manager

        // Set up viewModel, that contains reference to data
        val application = requireNotNull(this.activity).application
        val dataSource = TaskDatabase.getInstance(application).taskDatabaseDao
        val viewModelFactory = TaskTrackerViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(TaskTrackerViewModel::class.java)
        binding.viewModel = viewModel

        // Observe changes in the list
        binding.lifecycleOwner = this
        viewModel.tasks.observe(viewLifecycleOwner, {
            it?.let{
                adapter.submitList(it)
            }
        })

        viewModel.addButtonClicked.observe(viewLifecycleOwner, {
            if (it == true) {
                // Set up Alert Dialog
                val builder = AlertDialog.Builder(this.context!!)
                builder.setTitle(R.string.alert_title)
                val input = EditText(context)
                input.inputType = InputType.TYPE_CLASS_TEXT
                builder.setView(input)

                // Set up buttons
                builder.setPositiveButton("OK") { dialog, _ ->
                    run {
                        dialog.dismiss()
                        viewModel.addTask(input.text.toString())
                        Log.i("AlertDialog", "Text submitted")
                    }
                }
                builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                viewModel.onAddButtonClickedEnded()

                builder.show()
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