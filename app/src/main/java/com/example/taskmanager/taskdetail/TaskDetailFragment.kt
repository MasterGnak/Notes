package com.example.taskmanager.taskdetail


import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.database.Task
import com.example.taskmanager.databinding.FragmentTaskDetailBinding
import com.example.taskmanager.parseDeadline

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
        setHasOptionsMenu(true)

        return binding.root
    }


    override fun onStop() {
        super.onStop()
        updateTask()
    }

    private fun updateTask() {
        task.name = binding.editTextTaskName.text.toString()
        val deadline = binding.editTextDeadline.text.toString()
        try {
            val parsedDeadline = parseDeadline(deadline)
            task.date = parsedDeadline
        } catch(e: Exception) {
            Toast.makeText(this.context, "Неправильная дата", Toast.LENGTH_SHORT).show()
        }
        task.detail = binding.editTextTaskDetail.text.toString()
        viewModel.updateTask(task)

        val imm = requireNotNull(activity).getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.layout.windowToken, 0)
    }
}