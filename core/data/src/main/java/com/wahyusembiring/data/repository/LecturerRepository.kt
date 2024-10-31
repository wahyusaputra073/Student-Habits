package com.wahyusembiring.data.repository

import com.wahyusembiring.data.model.LecturerWithSubject
import com.wahyusembiring.data.model.entity.Lecturer
import kotlinx.coroutines.flow.Flow

interface LecturerRepository {

    fun getAllLecturer(): Flow<List<Lecturer>>

    fun getLecturerById(id: Int): Flow<Lecturer?>

    fun getAllLecturerWithSubjects(): Flow<List<LecturerWithSubject>>

    suspend fun insertLecturer(lecturer: Lecturer): Long

    suspend fun updateLecturer(lecturer: Lecturer)

    suspend fun deleteLecturer(id: Int) // Menambahkan metode untuk menghapus dosen

    suspend fun deletePhoneNumber(phoneNumber: String) // Menambahkan metode untuk menghapus nomor telepon
}
