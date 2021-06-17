package com.hera.todolist3.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hera.todolist3.data.Task
import com.hera.todolist3.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val dao: TaskDao
) : ViewModel() {

    val tasks = dao.getAllTasks().asLiveData()

    var taskPosition = 0

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