package com.wahyusembiring.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.wahyusembiring.data.model.entity.Thesis
import com.wahyusembiring.data.model.ThesisWithTask
import kotlinx.coroutines.flow.Flow

@Dao
interface ThesisDao {

    @Transaction
    @Query("SELECT * FROM thesis")
    fun getAllThesis(): Flow<List<ThesisWithTask>>

    @Transaction
    @Query("SELECT * FROM thesis WHERE id = :thesisId")
    fun getThesisById(thesisId: String): Flow<ThesisWithTask>

    @Insert(entity = Thesis::class)
    suspend fun insertThesis(thesis: Thesis): Long

    @Insert(entity = Thesis::class)
    suspend fun insertThesis(thesis: List<Thesis>): List<Long>

    @Upsert(entity = Thesis::class)
    suspend fun upsertThesis(thesis: Thesis)

    @Upsert(entity = Thesis::class)
    suspend fun upsertThesis(thesis: List<Thesis>)

    @Update(entity = Thesis::class)
    suspend fun updateThesis(thesis: Thesis)

    @Query("UPDATE thesis SET title = :title WHERE id = :thesisId")
    suspend fun updateThesisTitleById(thesisId: String, title: String)

    @Delete(entity = Thesis::class)
    suspend fun deleteThesis(thesis: Thesis)

    @Query("DELETE FROM thesis")
    suspend fun deleteAllThesis()

}