package com.wahyusembiring.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wahyusembiring.data.model.entity.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<Task>>

    @Insert(entity = Task::class)
    suspend fun insertTask(task: Task): Long

    @Insert(entity = Task::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(tasks: List<Task>): List<Long>

    @Update(entity = Task::class)
    suspend fun updateTask(task: Task)

    @Delete(entity = Task::class)
    suspend fun deleteTask(task: Task)

}