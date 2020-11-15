package com.example.taskmanager.tasktracker

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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
        binding.lifecycleOwner = this
        viewModel.tasks.observe(viewLifecycleOwner, {
            it?.let{
                adapter.submitList(it)
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
            viewModel.addTask()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}