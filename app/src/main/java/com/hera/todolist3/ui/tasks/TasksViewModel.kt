package com.hera.todolist3.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hera.todolist3.data.Task
import com.hera.todolist3.data.TaskDao
import com.hera.todolist3.util.Constants.BY_DATE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val dao: TaskDao
) : ViewModel() {

    val query = MutableStateFlow("")
    val orderBy = MutableStateFlow(BY_DATE)
    val tasks = combine(query, orderBy) { query, orderBy ->
        Pair(query, orderBy)
    }.flatMapLatest { (query, orderBy) ->
        dao.getAllTasks(query, orderBy)
    }


    fun insert(task: Task) = viewModelScope.launch {
        dao.insert(task)
    }


    fun delete(task: Task) = viewModelScope.launch {
        dao.delete(task)
    }


    fun update(task: Task) = viewModelScope.launch {
        dao.update(task)
    }


    fun deleteDone() = viewModelScope.launch {
        dao.deleteDone()
    }
}