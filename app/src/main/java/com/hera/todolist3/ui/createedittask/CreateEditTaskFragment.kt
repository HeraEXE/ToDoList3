package com.hera.todolist3.ui.createedittask

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.hera.todolist3.R
import com.hera.todolist3.data.Task
import com.hera.todolist3.databinding.FragmentCreateEditTaskBinding
import com.hera.todolist3.ui.MainActivity
import com.hera.todolist3.utils.CreateEditStatus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateEditTaskFragment : Fragment(R.layout.fragment_create_edit_task) {

    private val args: CreateEditTaskFragmentArgs by navArgs()

    private val viewModel: CreateEditTaskViewModel by viewModels()

    private lateinit var binding: FragmentCreateEditTaskBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (args.task != null) viewModel.createEditStatus = CreateEditStatus.EDIT
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCreateEditTaskBinding.bind(view)

        if (viewModel.createEditStatus == CreateEditStatus.CREATE) {
            (activity as AppCompatActivity).supportActionBar?.title = "Create task"

            binding.apply {
                fabCreateEditTask.setOnClickListener {
                    val name = etTaskName.text.toString()
                    val description = etTaskDescription.text.toString()

                    if (!validate(name)) return@setOnClickListener

                    val task = Task(name, description)

                    (activity as MainActivity).hideKeyboard(activity as MainActivity)
                    AlertDialog
                        .Builder(requireContext())
                        .setCancelable(false)
                        .setTitle("New task")
                        .setMessage("Are you sure you want to add new task?")
                        .setPositiveButton("Add") { dialog, _ ->
                            viewModel.insert(task)
                            dialog.dismiss()
                            findNavController().navigateUp()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()
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

                    (activity as MainActivity).hideKeyboard(activity as MainActivity)
                    AlertDialog
                        .Builder(requireContext())
                        .setCancelable(false)
                        .setTitle("Edit task")
                        .setMessage("Are you sure you want to edit this task?")
                        .setPositiveButton("Edit") { dialog, _ ->
                            viewModel.update(args.task!!)
                            dialog.dismiss()
                            findNavController().navigateUp()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show() 
                }
            }
        }
    }


    private fun validate(name: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            isValid = false
            binding.etTaskName.error = "empty"
        } else {
            binding.etTaskName.error = null
        }

        return isValid
    }
}