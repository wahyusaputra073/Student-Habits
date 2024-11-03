package com.wahyusembiring.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.wahyusembiring.data.model.SubjectWithExam
import com.wahyusembiring.data.model.SubjectWithExamAndHomework
import com.wahyusembiring.data.model.SubjectWithLecturer
import com.wahyusembiring.data.model.entity.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Transaction
    @Query("SELECT * FROM subject")
    fun getAllSubject(): Flow<List<Subject>>

    @Query("SELECT * FROM subject WHERE id = :id")
    fun getSubjectById(id: Int): Flow<Subject?>

    @Transaction
    @Query("SELECT * FROM subject WHERE id = :id")
    fun getSubjectWithLecturerById(id: Int): Flow<SubjectWithLecturer?>

    @Insert(entity = Subject::class)
    suspend fun insertSubject(subject: Subject): Long

    @Insert(entity = Subject::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSubject(subjects: List<Subject>): List<Long>

    @Update(entity = Subject::class)
    suspend fun updateSubject(subject: Subject)

    @Transaction
    @Query("SELECT * FROM subject")
    fun getAllSubjectWithExam(): Flow<List<SubjectWithExam>>

    @Transaction
    @Query("SELECT * FROM subject, exam WHERE exam.date >= :minDate AND exam.date <= :maxDate")
    fun getAllSubjectWithExam(minDate: Long, maxDate: Long): Flow<List<SubjectWithExam>>

    @Transaction
    @Query("SELECT * FROM subject")
    fun getSubjectWithExamAndHomework(): Flow<List<SubjectWithExamAndHomework>>

    @Query("DELETE FROM subject WHERE id = :id")
    suspend fun deleteSubjectById(id: Int)


    @Transaction
    @Query(
        "SELECT * " +
                "FROM subject, exam, homework " +
                "WHERE CASE WHEN :scored " +
                "THEN exam.score IS NOT NULL AND homework.score IS NOT NULL " +
                "ELSE exam.score IS NULL AND homework.score IS NULL " +
                "END"
    )
    fun getSubjectWithExamAndHomework(scored: Boolean): Flow<List<SubjectWithExamAndHomework>>
}