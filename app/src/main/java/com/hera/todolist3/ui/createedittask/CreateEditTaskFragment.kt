package com.hera.todolist3.ui.createedittask

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hera.todolist3.R
import com.hera.todolist3.data.Task
import com.hera.todolist3.databinding.FragmentCreateEditTaskBinding
import com.hera.todolist3.ui.MainActivity
import com.hera.todolist3.util.CreateEditStatus
import com.hera.todolist3.util.hideKeyboard
import com.hera.todolist3.util.showDatePickerDialog
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CreateEditTaskFragment : Fragment(R.layout.fragment_create_edit_task), DatePickerDialog.OnDateSetListener {

    private val args: CreateEditTaskFragmentArgs by navArgs()
    private val viewModel: CreateEditTaskViewModel by viewModels()
    private var _binding: FragmentCreateEditTaskBinding? = null
    private val binding get() = _binding!!
    private lateinit var task: Task


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.task == null) {
            viewModel.createEditStatus = CreateEditStatus.CREATE
            task = Task()
        } else {
            viewModel.createEditStatus = CreateEditStatus.EDIT
            task = args.task!!
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateEditTaskBinding.bind(view)
        showDateAndTime()
        binding.apply {
            if (viewModel.createEditStatus == CreateEditStatus.CREATE) {
                (activity as AppCompatActivity).supportActionBar?.title = "Create task"
            } else {
                (activity as AppCompatActivity).supportActionBar?.title = "Edit task"
                etTaskName.setText(task.name)
                etTaskDescription.setText(task.description)
            }
            llDatePicker.setOnClickListener {
                showDatePickerDialog(this@CreateEditTaskFragment)
            }
            fabCreateEditTask.setOnClickListener {
                createEditTask()
            }
        }
    }


    private fun validate(name: String): Boolean {
        var isValid = true
        if (name.isEmpty()) {
            isValid = false
            binding.ilTaskName.error = "Empty"
        } else {
            binding.ilTaskName.error = null
        }
        return isValid
    }


    private fun showDateAndTime() {
        val dateFormatter = SimpleDateFormat("MMMM dd, yyyy")
        val timeFormatter = SimpleDateFormat("hh:mm:ss a")
        val cal = Calendar.getInstance()
        cal.timeInMillis = task.date
        binding.tvDatePicker.text = dateFormatter.format(cal.time)
    }


    private fun createEditTask() {
        binding.apply {
            val name = etTaskName.text.toString()
            val description = etTaskDescription.text.toString()
            if (!validate(name)) return
            task.name = name
            task.description = description
            (activity as AppCompatActivity).hideKeyboard()
            val dialogLayout = layoutInflater.inflate(R.layout.dialog_layout, null)
            val dialog = AlertDialog
                .Builder(requireContext())
                .setView(dialogLayout)
                .create()
            val title: TextView = dialogLayout.findViewById(R.id.tv_title_dialog)
            val message: TextView = dialogLayout.findViewById(R.id.tv_message_dialog)
            val negativeBtn: TextView = dialogLayout.findViewById(R.id.tv_negative_dialog)
            val positiveBtn: TextView = dialogLayout.findViewById(R.id.tv_positive_dialog)
            if (viewModel.createEditStatus == CreateEditStatus.CREATE) {
                title.text = "New task"
                message.text = "Are you sure you want to add new task?"
                negativeBtn.text = "Cancel"
                positiveBtn.text = "Add"
                positiveBtn.setOnClickListener {
                    viewModel.insert(task)
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
            } else {
                title.text = "Edit task"
                message.text = "Are you sure you want to edit this task?"
                negativeBtn.text = "Cancel"
                positiveBtn.text = "Edit"
                positiveBtn.setOnClickListener {
                    viewModel.update(args.task!!)
                    dialog.dismiss()
                    findNavController().navigateUp()
                }
            }
            negativeBtn.setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
        }
    }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val formatter = SimpleDateFormat("MMMM dd, yyyy")
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        task.date = calendar.timeInMillis
        binding.tvDatePicker.text = formatter.format(calendar.time)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}