package com.wahyusembiring.data.repository.implementation

import com.wahyusembiring.data.Result
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LecturerRepositoryImpl @Inject constructor(
    private val lecturerDao: LecturerDao,
    private val lecturerService: LecturerService,
    private val subjectDao: SubjectDao,
) : LecturerRepository {

    private suspend fun cacheLecturer() {
        val lecturers = lecturerService.getAllLecturerWithSubject()
        lecturerDao.getAllLecturer().first().forEach { lecturer ->
            if (lecturers.none { it.lecturer.id == lecturer.id }) {
                lecturerDao.deleteLecturerById(lecturer.id)
            }
        }
        lecturerDao.upsertLecturer(lecturers.map { it.lecturer })
        val subjects = lecturers.map { it.subjects }.flatten().distinctBy { it.id }
        subjectDao.upsertSubject(subjects = subjects)
    }

    override fun getAllLecturer(): Flow<Result<Flow<List<Lecturer>>>> {
        return flow {
            emit(Result.Loading())
            cacheLecturer()
            emit(Result.Success(lecturerDao.getAllLecturer()))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getAllLecturerWithSubjects(): Flow<Result<Flow<List<LecturerWithSubject>>>> {
        return flow {
            emit(Result.Loading())
            cacheLecturer()
            emit(Result.Success(lecturerDao.getAllLecturerWithSubject()))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun insertLecturer(lecturer: Lecturer): Flow<Result<String>> {
        return flow {
            emit(Result.Loading())
            lecturerService.saveLecturer(lecturer)
            lecturerDao.insertLecturer(lecturer)
            emit(Result.Success(lecturer.id))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getLecturerById(id: String): Flow<Result<Flow<Lecturer?>>> {
        return flow {
            emit(Result.Loading())
            val lecturer = lecturerService.getLecturerById(id)
            lecturerDao.updateLecturer(lecturer)
            emit(Result.Success(lecturerDao.getLecturerById(id)))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun updateLecturer(lecturer: Lecturer): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            lecturerService.saveLecturer(lecturer)
            lecturerDao.updateLecturer(lecturer)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun deleteLecturer(id: String): Flow<Result<Unit>> {
        return flow<Result<Unit>> {
            emit(Result.Loading())
            lecturerService.deleteLecturerById(id)
            lecturerDao.deleteLecturerById(id)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

     override fun deletePhoneNumber(phoneNumber: String): Flow<Result<Unit>> {
         return flow {
             emit(Result.Loading())
             lecturerDao.deletePhoneNumber(phoneNumber) // Panggilan ke DAO
             emit(Result.Success(Unit))
         }.catch {
             emit(Result.Error(it))
         }
    }
}