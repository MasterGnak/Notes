package com.example.taskmanager.taskdetail


import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.taskmanager.R
import com.example.taskmanager.databinding.FragmentTaskDetailBinding

class TaskDetailFragment : Fragment() {

    private lateinit var viewModel: TaskDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(activity).application
        val task = TaskDetailFragmentArgs.fromBundle(arguments!!).selectedTask
        viewModel = ViewModelProvider(this, TaskDetailViewModelFactory(task, application)).get(TaskDetailViewModel::class.java)
        val binding = FragmentTaskDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setHasOptionsMenu(true)
        val imm = requireNotNull(activity).getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        viewModel.editable.observe(viewLifecycleOwner, {
            activity?.invalidateOptionsMenu()
        })

        viewModel.doneButtonClicked.observe(viewLifecycleOwner, {
            if (it) {
                task.name = binding.editTextTaskName.text.toString()
                val deadline = binding.editTextDeadline.text.toString()
                try {
                    task.deadline = viewModel.parseDeadline(deadline)
                } catch(e: Exception) {
                    Toast.makeText(this.context, e.message, Toast.LENGTH_SHORT).show()
                }
                task.detail = binding.editTextTaskDetail.text.toString()
                viewModel.updateTask(task)
                viewModel.onDoneButtonFinish()
                viewModel.disableEditing()
                imm.hideSoftInputFromWindow(binding.layout.windowToken, 0)
                binding.invalidateAll()
            }

        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_task_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_done) {
            viewModel.onDoneButtonClicked()
            return true
        }
        if (item.itemId == R.id.edit) {
            viewModel.enableEditing()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val doneButton = menu.findItem(R.id.edit_done)
        val editButton = menu.findItem(R.id.edit)
        if (viewModel.editable.value!!) {
            doneButton.isEnabled = true
            doneButton.isVisible = true
            editButton.isEnabled = false
            editButton.isVisible = false
        } else {
            doneButton.isEnabled = false
            doneButton.isVisible = false
            editButton.isEnabled = true
            editButton.isVisible = true
        }
    }
}