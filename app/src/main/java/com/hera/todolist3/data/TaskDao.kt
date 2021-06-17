package com.hera.todolist3.data

import androidx.room.*
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


    @Query("SELECT * FROM task_table ORDER BY date")
    fun getAllTasks(): Flow<List<Task>>
}