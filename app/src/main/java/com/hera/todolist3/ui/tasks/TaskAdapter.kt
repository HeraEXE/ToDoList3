package com.hera.todolist3.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hera.todolist3.data.Task
import com.hera.todolist3.databinding.ItemTaskBinding


class TaskAdapter(private val listener: Listener)
    : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    private var tasks = listener.getAllTasks()


    interface Listener {

        fun getAllTasks(): List<Task>

        fun updateTask(task: Task, position: Int)

        fun navigateToEdit(task: Task, position: Int)
    }


    inner class ViewHolder(val binding: ItemTaskBinding)
        : RecyclerView.ViewHolder(binding.root) {
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    override fun getItemCount() = tasks.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]

        holder.binding.apply {

            checkBoxTask.isChecked = task.isDone
            tvNameTask.text = task.name
            tvDateTask.text = task.dateFormatted

            checkBoxTask.setOnClickListener {
                task.isDone = !task.isDone
                listener.updateTask(task, position)
            }

            tvNameTask.setOnClickListener {
                listener.navigateToEdit(task, position)
            }

            tvDateTask.setOnClickListener {
                listener.navigateToEdit(task, position)
            }
        }
    }

    fun updateTasks() {
        tasks = listener.getAllTasks()
    }
}