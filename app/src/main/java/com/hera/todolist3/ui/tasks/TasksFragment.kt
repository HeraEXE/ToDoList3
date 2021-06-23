package com.hera.todolist3.ui.tasks

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hera.todolist3.R
import com.hera.todolist3.data.Task
import com.hera.todolist3.databinding.FragmentTasksBinding
import com.hera.todolist3.utils.DatabaseOrder
import dagger.hilt.android.AndroidEntryPoint


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

        binding = FragmentTasksBinding.bind(view)

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            adapter.differ.submitList(tasks)
        }

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


    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar?.title = "Tasks"
    }


    private val itemTouchHelper = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val task = viewModel.tasks.value!![viewHolder.adapterPosition]

            AlertDialog
                .Builder(requireContext())
                .setCancelable(false)
                .setTitle("Delete task")
                .setMessage("Do you want to delete this task?")
                .setPositiveButton("Delete") { dialog, _ ->
                    viewModel.delete(task)
                    Snackbar.make(binding.root, "Task was deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            viewModel.insert(task)
                        }
                        .show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    adapter.notifyItemChanged(viewHolder.adapterPosition)
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
            true
        }
        R.id.action_order_by_name -> {
            viewModel.databaseOrder.value = DatabaseOrder.BY_NAME
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun updateTask(task: Task) {
        viewModel.update(task)
    }


    override fun onTaskClick(task: Task) {
        val action = TasksFragmentDirections.actionTasksFragmentToCreateEditTaskFragment(task)
        findNavController().navigate(action)
    }
}