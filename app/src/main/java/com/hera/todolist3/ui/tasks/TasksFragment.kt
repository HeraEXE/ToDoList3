package com.hera.todolist3.ui.tasks

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.hera.todolist3.R
import com.hera.todolist3.data.Task
import com.hera.todolist3.databinding.FragmentTasksBinding
import com.hera.todolist3.util.Constants.BY_DATE
import com.hera.todolist3.util.Constants.BY_DATE_DESC
import com.hera.todolist3.util.Constants.BY_NAME
import com.hera.todolist3.util.Constants.BY_NAME_DESC
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.Listener {

    private val viewModel: TasksViewModel by viewModels()
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TaskAdapter
    private lateinit var sharedPrefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        sharedPrefs = (activity as AppCompatActivity)
            .getSharedPreferences("settings", Context.MODE_PRIVATE)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.orderBy.value = sharedPrefs.getInt("order", BY_DATE)
        _binding = FragmentTasksBinding.bind(view)
        adapter = TaskAdapter(this)
        binding.apply {
            recycler.layoutManager = LinearLayoutManager(requireContext())
            recycler.adapter = adapter
            ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recycler)
            fabNewTask.setOnClickListener {
                val action = TasksFragmentDirections.actionTasksFragmentToCreateEditTaskFragment(title = "Create task")
                findNavController().navigate(action)
            }
        }
        lifecycleScope.launch {
            viewModel.tasks.collect { tasks ->
                adapter.differ.submitList(tasks)
            }
        }
    }


    private val itemTouchHelper = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val task = adapter.differ.currentList[viewHolder.adapterPosition]
            val dialogLayout = layoutInflater.inflate(R.layout.dialog_layout, null)
            val dialog = AlertDialog
                .Builder(requireContext())
                .setView(dialogLayout)
                .create()
            val title: TextView = dialogLayout.findViewById(R.id.tv_title_dialog)
            val message: TextView = dialogLayout.findViewById(R.id.tv_message_dialog)
            val negativeBtn: TextView = dialogLayout.findViewById(R.id.tv_negative_dialog)
            val positiveBtn: TextView = dialogLayout.findViewById(R.id.tv_positive_dialog)
            title.text = "Delete task"
            message.text = "Do you want to delete this task?"
            negativeBtn.text = "Cancel"
            positiveBtn.text = "Delete"
            negativeBtn.setOnClickListener {
                adapter.notifyItemChanged(viewHolder.adapterPosition)
                    dialog.dismiss()
            }
            positiveBtn.setOnClickListener {
                viewModel.delete(task)
                    Snackbar.make(binding.root, "Task was deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO") {
                            viewModel.insert(task)
                        }
                        .show()
                    dialog.dismiss()
            }
            dialog.show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_tasks, menu)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        val editText: EditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text)
        editText.setTextColor(resources.getColor(R.color.white))
        searchView.queryHint = "Search..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null)
                    viewModel.query.value = newText
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.action_delete_done -> {
            val dialogLayout = layoutInflater.inflate(R.layout.dialog_layout, null)
            val dialog = AlertDialog
                .Builder(requireContext())
                .setView(dialogLayout)
                .create()
            val title: TextView = dialogLayout.findViewById(R.id.tv_title_dialog)
            val message: TextView = dialogLayout.findViewById(R.id.tv_message_dialog)
            val negativeBtn: TextView = dialogLayout.findViewById(R.id.tv_negative_dialog)
            val positiveBtn: TextView = dialogLayout.findViewById(R.id.tv_positive_dialog)
            title.text = "Delete done tasks"
            message.text = "Do you want to delete all done tasks?"
            negativeBtn.text = "Cancel"
            positiveBtn.text = "Delete"
            negativeBtn.setOnClickListener {
                dialog.dismiss()
            }
            positiveBtn.setOnClickListener {
                viewModel.deleteDone()
                dialog.dismiss()
            }
            dialog.show()
            true
        }
        R.id.action_order_by_date -> {
            viewModel.orderBy.value = BY_DATE
            saveInSharedPrefs(BY_DATE)
            true
        }
        R.id.action_order_by_date_desc -> {
            viewModel.orderBy.value = BY_DATE_DESC
            saveInSharedPrefs(BY_DATE_DESC)
            true
        }
        R.id.action_order_by_name -> {
            viewModel.orderBy.value = BY_NAME
            saveInSharedPrefs(BY_NAME)
            true
        }
        R.id.action_order_by_name_desc -> {
            viewModel.orderBy.value = BY_NAME_DESC
            saveInSharedPrefs(BY_NAME_DESC)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }


    override fun updateTask(task: Task) {
        viewModel.update(task)
    }


    override fun onTaskClick(task: Task) {
        val action = TasksFragmentDirections.actionTasksFragmentToCreateEditTaskFragment(task = task, title= "Edit task")
        findNavController().navigate(action)
    }


    private fun saveInSharedPrefs(value: Int) {
        sharedPrefs.edit().apply {
            putInt("order", value)
            apply()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}