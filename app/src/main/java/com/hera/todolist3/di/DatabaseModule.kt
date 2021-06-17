package com.hera.todolist3.di

import android.content.Context
import androidx.room.Room
import com.hera.todolist3.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room
        .databaseBuilder(context, TaskDatabase::class.java, "task_database")
        .build()


    @Provides
    fun provideDao(
        db: TaskDatabase
    ) = db.taskDao()
}