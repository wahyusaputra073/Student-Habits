package com.wahyusembiring.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wahyusembiring.data.model.LecturerWithSubject
import com.wahyusembiring.data.model.entity.Lecturer
import kotlinx.coroutines.flow.Flow

@Dao
interface LecturerDao {

    @Query("SELECT * FROM lecturer")
    fun getAllLecturer(): Flow<List<Lecturer>>

    @Query("SELECT * FROM lecturer")
    fun getAllLecturerWithSubject(): Flow<List<LecturerWithSubject>>

    @Insert(entity = Lecturer::class)
    suspend fun insertLecturer(lecturer: Lecturer): Long

    @Insert(entity = Lecturer::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLecturer(lecturers: List<Lecturer>): List<Long>

    @Query("SELECT * FROM lecturer WHERE id = :id")
    fun getLecturerById(id: Int): Flow<Lecturer?>

    @Update(entity = Lecturer::class)
    suspend fun updateLecturer(lecturer: Lecturer)

    @Query("DELETE FROM lecturer WHERE id = :lecturerId")
    suspend fun deleteLecturerById(lecturerId: String)

    @Query("DELETE FROM lecturer WHERE phone = :phoneNumber")
    suspend fun deletePhoneNumber(phoneNumber: String)


//    @Query("SELECT * FROM lecturer")
//    fun getAllLecturerWithSubjects(): Flow<List<LecturerWithSubject>>
}