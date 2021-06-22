package com.hera.todolist3.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hera.todolist3.data.Task
import com.hera.todolist3.data.TaskDao
import com.hera.todolist3.utils.DatabaseOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val dao: TaskDao
) : ViewModel() {

    val databaseOrder = MutableStateFlow(DatabaseOrder.BY_DATE)

    val tasks = databaseOrder.flatMapLatest { orderBy ->
        dao.getAllTasks(orderBy)
    }.asLiveData()


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