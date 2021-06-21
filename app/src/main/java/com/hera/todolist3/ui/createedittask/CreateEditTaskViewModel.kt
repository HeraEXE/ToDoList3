package com.hera.todolist3.ui.createedittask

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hera.todolist3.data.Task
import com.hera.todolist3.data.TaskDao
import com.hera.todolist3.utils.CreateEditStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateEditTaskViewModel @Inject constructor(
    private val dao: TaskDao
) : ViewModel() {

    var createEditStatus = CreateEditStatus.CREATE

    fun insert(task: Task) = viewModelScope.launch {
        dao.insert(task)
    }


    fun update(task: Task) = viewModelScope.launch {
        dao.update(task)
    }
}