package com.example.taskmanager.tasktracker

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskmanager.R
import com.example.taskmanager.database.Task
import com.example.taskmanager.database.TaskDatabase
import com.example.taskmanager.databinding.FragmentTaskTrackerBinding
import kotlinx.android.synthetic.main.task_name_edit_text.*
import kotlinx.android.synthetic.main.task_name_edit_text.view.*

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
                viewModel.onAddButtonClickedEnded()
            }
        })

        setHasOptionsMenu(true)
        return binding.root
    }

    private fun getAlertDialog(
        positiveLambda: ((dialog: DialogInterface, _: Int) -> Unit)?,
        negativeLambda: ((dialog: DialogInterface, _: Int) -> Unit)?,
        title: String
    ): AlertDialog {
        val builder = AlertDialog.Builder(this.context!!)
        builder.setTitle(title)
        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)


        // Set up buttons
        builder.setPositiveButton("OK", positiveLambda)
        builder.setNegativeButton("Cancel", negativeLambda)


        return builder.create()
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