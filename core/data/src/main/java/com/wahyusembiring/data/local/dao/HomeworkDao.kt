package com.wahyusembiring.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Homework
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeworkDao {

    @Query("SELECT * FROM homework")
    fun getAllHomework(): Flow<List<Homework>>

    @Insert(entity = Homework::class)
    suspend fun insertHomework(homework: Homework): Long

    @Insert(entity = Homework::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHomework(homeworks: List<Homework>): List<Long>

    @Update(entity = Homework::class)
    suspend fun updateHomework(homework: Homework)

    @Transaction
    @Query(
        "SELECT * FROM homework "
    )
    fun getAllHomeworkWithSubject(): Flow<List<HomeworkWithSubject>>

    @Transaction
    @Query(
        "SELECT * " +
                "FROM homework " +
                "WHERE due_date BETWEEN :minDate AND :maxDate " +
                "ORDER BY due_date ASC"
    )
    fun getAllHomeworkWithSubject(
        minDate: Long,
        maxDate: Long
    ): Flow<List<HomeworkWithSubject>>

    @Transaction
    @Query("SELECT * FROM homework WHERE id = :id")
    fun getHomeworkById(id: Int): Flow<HomeworkWithSubject?>

    @Delete(entity = Homework::class)
    suspend fun deleteHomework(homework: Homework)

}