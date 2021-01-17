package com.example.taskmanager.taskdetail


import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.calendar
import com.example.taskmanager.database.Task
import com.example.taskmanager.databinding.FragmentTaskDetailBinding
import java.util.*

class TaskDetailFragment : Fragment() {

    private lateinit var viewModel: TaskDetailViewModel
    private lateinit var task: Task
    private lateinit var binding: FragmentTaskDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(activity).application
        task = TaskDetailFragmentArgs.fromBundle(requireArguments()).selectedTask
        viewModel = ViewModelProvider(this, TaskDetailViewModelFactory(task, application)).get(TaskDetailViewModel::class.java)
        binding = FragmentTaskDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.dateClicked.observe(viewLifecycleOwner) {
            if (it) {
                showDatePicker()
                viewModel.onDateClickedFinish()
            }
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onStop() {
        super.onStop()
        updateTask()
    }

    private fun updateTask() {
        task.name = binding.editTextTaskName.text.toString()
        task.detail = binding.editTextTaskDetail.text.toString()
        viewModel.updateTask(task)

        val imm = requireNotNull(activity).getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.layout.windowToken, 0)
    }

    private fun showDatePicker() {
        val picker = DatePickerDialog(this.requireContext(),
            { _, year, month, dayOfMonth ->
                val setDate = Calendar.getInstance()
                setDate.set(Calendar.YEAR, year)
                setDate.set(Calendar.MONTH, month)
                setDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                task.date = setDate.time.time
                viewModel.updateTask(task)
                binding.invalidateAll()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))
        picker.show()
    }
}