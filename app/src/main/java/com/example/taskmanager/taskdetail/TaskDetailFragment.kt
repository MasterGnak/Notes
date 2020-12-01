package com.example.taskmanager.taskdetail


import android.app.Activity
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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

        viewModel.textViewClicked.observe(viewLifecycleOwner, {
            activity?.invalidateOptionsMenu()
            if (it != null) {
                val view = requireNotNull(activity).findViewById<EditText>(it)
                view.requestFocus()
                imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
            }
        })

        viewModel.doneButtonClicked.observe(viewLifecycleOwner, {
            if (it) {
                task.name = binding.editTextTaskName.text.toString()
                task.deadline = binding.editTextDeadline.text.toString()
                task.detail = binding.editTextTaskDetail.text.toString()
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
        inflater.inflate(R.menu.menu_done_task_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_done) {
            viewModel.onDoneButtonClicked()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val doneButton = menu.findItem(R.id.edit_done)
        if (viewModel.textViewClicked.value == null) {
            doneButton.isEnabled = false
            doneButton.isVisible = false
        } else {
            doneButton.isEnabled = true
            doneButton.isVisible = true
        }
    }
}