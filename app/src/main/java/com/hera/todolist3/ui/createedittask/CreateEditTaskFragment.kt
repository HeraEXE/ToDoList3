package com.hera.todolist3.ui.createedittask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.TextView
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEditTaskFragment : Fragment(R.layout.fragment_create_edit_task) {

    private val args: CreateEditTaskFragmentArgs by navArgs()
    private val viewModel: CreateEditTaskViewModel by viewModels()
    private var _binding: FragmentCreateEditTaskBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.task != null) viewModel.createEditStatus = CreateEditStatus.EDIT
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCreateEditTaskBinding.bind(view)
        if (viewModel.createEditStatus == CreateEditStatus.CREATE) {
            (activity as AppCompatActivity).supportActionBar?.title = "Create task"
            binding.apply {
                fabCreateEditTask.setOnClickListener {
                    val name = etTaskName.text.toString()
                    val description = etTaskDescription.text.toString()
                    if (!validate(name)) return@setOnClickListener
                    val task = Task(name, description)
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
                    title.text = "New task"
                    message.text = "Are you sure you want to add new task?"
                    negativeBtn.text = "Cancel"
                    positiveBtn.text = "Add"
                    negativeBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                    positiveBtn.setOnClickListener {
                        viewModel.insert(task)
                        dialog.dismiss()
                        findNavController().navigateUp()
                    }
                    dialog.show()
                }
            }
        } else {
            (activity as AppCompatActivity).supportActionBar?.title = "Edit task"
            binding.apply {
                etTaskName.setText(args.task?.name)
                etTaskDescription.setText(args.task?.description)
                fabCreateEditTask.setOnClickListener {
                    val name = etTaskName.text.toString()
                    val description = etTaskDescription.text.toString()
                    if (!validate(name)) return@setOnClickListener
                    args.task?.name = name
                    args.task?.description = description
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
                    title.text = "Edit task"
                    message.text = "Are you sure you want to edit this task?"
                    negativeBtn.text = "Cancel"
                    positiveBtn.text = "Edit"
                    negativeBtn.setOnClickListener {
                        dialog.dismiss()
                    }
                    positiveBtn.setOnClickListener {
                        viewModel.update(args.task!!)
                        dialog.dismiss()
                        findNavController().navigateUp()
                    }
                    dialog.show()
                }
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}