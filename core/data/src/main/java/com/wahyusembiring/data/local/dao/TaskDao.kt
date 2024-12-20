package com.wahyusembiring.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.wahyusembiring.data.model.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(entity = Task::class)
    suspend fun insertTask(task: Task): Long

    @Insert(entity = Task::class)
    suspend fun insertTask(tasks: List<Task>): List<Long>

    @Upsert(entity = Task::class)
    suspend fun upsertTask(task: Task)

    @Upsert(entity = Task::class)
    suspend fun upsertTask(tasks: List<Task>)

    @Update(entity = Task::class)
    suspend fun updateTask(task: Task)

    @Delete(entity = Task::class)
    suspend fun deleteTask(task: Task)

    @Query("DELETE FROM task")
    suspend fun deleteAllTask()

}