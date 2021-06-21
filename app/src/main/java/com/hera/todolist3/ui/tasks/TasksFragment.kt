package com.hera.todolist3.ui.tasks

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hera.todolist3.R
import com.hera.todolist3.data.Task
import com.hera.todolist3.databinding.FragmentTasksBinding
import com.hera.todolist3.utils.DatabaseOrder
import com.hera.todolist3.utils.ObserverStatus
import dagger.hilt.android.AndroidEntryPoint

var observerStatus = ObserverStatus.SLEEP

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.Listener {

    private val viewModel: TasksViewModel by viewModels()

    private lateinit var binding: FragmentTasksBinding

    private lateinit var adapter: TaskAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Tasks"

        binding = FragmentTasksBinding.bind(view)

        viewModel.tasks.observe(viewLifecycleOwner, Observer {
            adapter.updateTasks()

            when (observerStatus) {
                ObserverStatus.INSERT -> {
                    adapter.notifyItemInserted(it.size-1)
                }
                ObserverStatus.UPDATE -> {
                    adapter.notifyItemChanged(viewModel.taskPosition)
                }
                ObserverStatus.DELETE -> {
                    adapter.notifyItemRemoved(viewModel.taskPosition)
                }
                ObserverStatus.RESTORE -> {
                    adapter.notifyItemInserted(viewModel.taskPosition)
                }
                ObserverStatus.DELETE_DONE -> {
                    adapter.notifyDataSetChanged()
                }
                ObserverStatus.SLEEP -> {
                    adapter.notifyDataSetChanged()
                }
            }
        })

        adapter = TaskAdapter(this)

        binding.apply {
            recycler.layoutManager = LinearLayoutManager(requireContext())
            recycler.adapter = adapter
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recycler)

            fabNewTask.setOnClickListener {
                val action = TasksFragmentDirections.actionTasksFragmentToCreateEditTaskFragment()
                findNavController().navigate(action)
            }
        }

    }


    private val itemTouchHelper = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {


        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false


        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

            viewModel.taskPosition = viewHolder.adapterPosition
            val task = viewModel.tasks.value!![viewModel.taskPosition]

            AlertDialog
                .Builder(requireContext())
                .setCancelable(false)
                .setTitle("Delete task")
                .setMessage("Do you want to delete this task?")
                .setPositiveButton("Delete") { dialog, _ ->
                    observerStatus = ObserverStatus.DELETE
                    viewModel.delete(task)
                    Snackbar.make(binding.root, "Task was deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            observerStatus = ObserverStatus.RESTORE
                            viewModel.insert(task)
                        }
                        .show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    adapter.notifyItemChanged(viewModel.taskPosition)
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tasks, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {

        R.id.action_delete_done -> {
            AlertDialog
                .Builder(requireContext())
                .setCancelable(false)
                .setTitle("Delete done tasks")
                .setMessage("Do you want to delete all done task?")
                .setPositiveButton("Delete") { dialog, _ ->
                    observerStatus = ObserverStatus.DELETE_DONE
                    viewModel.deleteDone()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()
                .show()
            true
        }
        R.id.action_order_by_date -> {
            viewModel.databaseOrder.value = DatabaseOrder.BY_DATE
            observerStatus = ObserverStatus.SLEEP
            true
        }
        R.id.action_order_by_name -> {
            viewModel.databaseOrder.value = DatabaseOrder.BY_NAME
            observerStatus = ObserverStatus.SLEEP
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun getAllTasks() = viewModel.tasks.value ?: listOf()


    override fun updateTask(task: Task, position: Int) {
        observerStatus = ObserverStatus.UPDATE
        viewModel.taskPosition = position
        viewModel.update(task)
    }


    override fun navigateToEdit(task: Task, position: Int) {
        viewModel.taskPosition = position

        val action = TasksFragmentDirections.actionTasksFragmentToCreateEditTaskFragment(task)
        findNavController().navigate(action)
    }
}