package com.hera.todolist3.ui.tasks

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hera.todolist3.data.Task
import com.hera.todolist3.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*


class TaskAdapter(private val listener: Listener)
    : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {



    inner class ViewHolder(private val binding: ItemTaskBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val task = differ.currentList[position]
            binding.apply {
                checkBoxTask.isChecked = task.isDone
                tvNameTask.text = task.name
                tvDateTask.text = getFormattedDate(task.date)

                checkBoxTask.setOnClickListener {
                    task.isDone = !task.isDone
                    listener.updateTask(task)
                }
                clItemTask.setOnClickListener {
                    listener.onTaskClick(task)
                }
            }
        }


        private fun getFormattedDate(date: Long): String {
            val formatter = SimpleDateFormat("MMMM dd, yyyy")
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            return formatter.format(cal.time)
        }
    }



    interface Listener {

        fun updateTask(task: Task)


        fun onTaskClick(task: Task)
    }



    private val diffCallback = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
    val differ = AsyncListDiffer(this, diffCallback)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun getItemCount() = differ.currentList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(position)
}