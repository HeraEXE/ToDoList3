package com.hera.todolist3.data

import androidx.room.*
import com.hera.todolist3.util.Constants.BY_DATE
import com.hera.todolist3.util.Constants.BY_DATE_DESC
import com.hera.todolist3.util.Constants.BY_NAME
import com.hera.todolist3.util.Constants.BY_NAME_DESC
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)


    @Delete
    suspend fun delete(task: Task)


    @Update
    suspend fun update(task: Task)


    @Query("DELETE FROM task_table WHERE isDone = 1")
    suspend fun deleteDone()


    fun getAllTasks(orderBy: Int) = when(orderBy) {
        BY_DATE -> getAllTasksOrderedByDate()
        BY_DATE_DESC -> getAllTasksOrderedByDateDesc()
        BY_NAME -> getAllTasksOrderedByName()
        BY_NAME_DESC -> getAllTasksOrderedByNameDEsc()
        else -> getAllTasksOrderedByDate()
    }


    @Query("SELECT * FROM task_table ORDER BY date")
    fun getAllTasksOrderedByDate(): Flow<List<Task>>


    @Query("SELECT * FROM task_table ORDER BY date DESC")
    fun getAllTasksOrderedByDateDesc(): Flow<List<Task>>


    @Query("SELECT * FROM task_table ORDER BY name")
    fun getAllTasksOrderedByName(): Flow<List<Task>>


    @Query("SELECT * FROM task_table ORDER BY name DESC")
    fun getAllTasksOrderedByNameDEsc(): Flow<List<Task>>
}