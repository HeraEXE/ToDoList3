package com.hera.todolist3.ui.tasks

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hera.todolist3.R
import com.hera.todolist3.databinding.FragmentTasksBinding


class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private lateinit var binding: FragmentTasksBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTasksBinding.bind(view)
        binding.apply {
            fabNewTask.setOnClickListener {
                findNavController().navigate(R.id.action_tasksFragment_to_createEditTaskFragment)
            }
        }
    }
}