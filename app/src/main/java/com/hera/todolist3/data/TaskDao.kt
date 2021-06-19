package com.hera.todolist3.data

import androidx.room.*
import com.hera.todolist3.utils.DatabaseOrder
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


    fun getAllTasks(orderBy: DatabaseOrder) = when(orderBy) {
        DatabaseOrder.BY_DATE -> getAllTasksOrderedByDate()
        DatabaseOrder.BY_NAME -> getAllTasksOrderedByName()
    }


    @Query("SELECT * FROM task_table ORDER BY date")
    fun getAllTasksOrderedByDate(): Flow<List<Task>>


    @Query("SELECT * FROM task_table ORDER BY name")
    fun getAllTasksOrderedByName(): Flow<List<Task>>
}