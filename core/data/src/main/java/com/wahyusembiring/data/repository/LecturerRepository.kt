package com.wahyusembiring.data.repository

import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.LecturerWithSubject
import com.wahyusembiring.data.model.entity.Lecturer
import kotlinx.coroutines.flow.Flow

interface LecturerRepository {

    fun getAllLecturer(): Flow<Result<Flow<List<Lecturer>>>>

    fun getLecturerById(id: String): Flow<Result<Flow<Lecturer?>>>

    fun getAllLecturerWithSubjects(): Flow<Result<Flow<List<LecturerWithSubject>>>>

    fun insertLecturer(lecturer: Lecturer): Flow<Result<String>>

    fun updateLecturer(lecturer: Lecturer): Flow<Result<Unit>>

    fun deleteLecturer(id: String): Flow<Result<Unit>> // Menambahkan metode untuk menghapus dosen

    fun deletePhoneNumber(phoneNumber: String): Flow<Result<Unit>> // Menambahkan metode untuk menghapus nomor telepon
    
}
