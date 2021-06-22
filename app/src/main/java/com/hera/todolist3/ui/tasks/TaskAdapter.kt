package com.hera.todolist3.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hera.todolist3.data.Task
import com.hera.todolist3.databinding.ItemTaskBinding


class TaskAdapter(private val listener: Listener)
    : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTaskBinding)
        : RecyclerView.ViewHolder(binding.root) {
    }

    interface Listener {

        fun updateTask(task: Task)

        fun onTaskClick(task: Task)
    }


    private val differCallback = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }

    val differ = AsyncListDiffer(this, differCallback)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }


    override fun getItemCount() = differ.currentList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = differ.currentList[position]

        holder.binding.apply {

            checkBoxTask.isChecked = task.isDone
            tvNameTask.text = task.name
            tvDateTask.text = task.dateFormatted

            checkBoxTask.setOnClickListener {
                task.isDone = !task.isDone
                listener.updateTask(task)
            }

            tvNameTask.setOnClickListener {
                listener.onTaskClick(task)
            }

            tvDateTask.setOnClickListener {
                listener.onTaskClick(task)
            }
        }
    }
}