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


    fun getAllTasks(query: String, orderBy: Int) = when(orderBy) {
        BY_DATE -> getAllTasksOrderedByDate(query)
        BY_DATE_DESC -> getAllTasksOrderedByDateDesc(query)
        BY_NAME -> getAllTasksOrderedByName(query)
        BY_NAME_DESC -> getAllTasksOrderedByNameDEsc(query)
        else -> getAllTasksOrderedByDate(query)
    }


    @Query("SELECT * FROM task_table WHERE name LIKE '%' || :query || '%' ORDER BY date")
    fun getAllTasksOrderedByDate(query: String): Flow<List<Task>>


    @Query("SELECT * FROM task_table WHERE name LIKE '%' || :query || '%' ORDER BY date DESC")
    fun getAllTasksOrderedByDateDesc(query: String): Flow<List<Task>>


    @Query("SELECT * FROM task_table WHERE name LIKE '%' || :query || '%' ORDER BY name")
    fun getAllTasksOrderedByName(query: String): Flow<List<Task>>


    @Query("SELECT * FROM task_table WHERE name LIKE '%' || :query || '%' ORDER BY name DESC")
    fun getAllTasksOrderedByNameDEsc(query: String): Flow<List<Task>>
}