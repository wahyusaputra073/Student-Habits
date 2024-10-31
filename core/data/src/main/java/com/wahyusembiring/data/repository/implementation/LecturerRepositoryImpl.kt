package com.wahyusembiring.data.repository.implementation

import com.wahyusembiring.data.local.dao.LecturerDao
import com.wahyusembiring.data.local.dao.SubjectDao
import com.wahyusembiring.data.model.LecturerWithSubject
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.data.remote.LecturerService
import com.wahyusembiring.data.repository.LecturerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LecturerRepositoryImpl @Inject constructor(
    private val lecturerDao: LecturerDao,
) : LecturerRepository {

    override fun getAllLecturer(): Flow<List<Lecturer>> {
        return lecturerDao.getAllLecturer()
    }

    override fun getAllLecturerWithSubjects(): Flow<List<LecturerWithSubject>> {
        return lecturerDao.getAllLecturerWithSubject()
    }

    override suspend fun insertLecturer(lecturer: Lecturer): Long {
        return lecturerDao.insertLecturer(lecturer)
    }

    override fun getLecturerById(id: Int): Flow<Lecturer?> {
        return lecturerDao.getLecturerById(id)
    }

    override suspend fun updateLecturer(lecturer: Lecturer) {
        lecturerDao.updateLecturer(lecturer)
    }

    override suspend fun deleteLecturer(id: Int) {
        lecturerDao.deleteLecturerById(id.toString()) // Pastikan untuk menghapus dosen dari database
    }

     override suspend fun deletePhoneNumber(phoneNumber: String) {
        lecturerDao.deletePhoneNumber(phoneNumber) // Panggilan ke DAO
    }
}