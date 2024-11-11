package com.wahyusembiring.data.repository.implementation

import com.wahyusembiring.data.Result
import com.wahyusembiring.data.local.dao.ExamDao
import com.wahyusembiring.data.local.dao.HomeworkDao
import com.wahyusembiring.data.local.dao.LecturerDao
import com.wahyusembiring.data.local.dao.SubjectDao
import com.wahyusembiring.data.model.SubjectWithExam
import com.wahyusembiring.data.model.SubjectWithExamAndHomework
import com.wahyusembiring.data.model.SubjectWithLecturer
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.remote.SubjectService
import com.wahyusembiring.data.repository.SubjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao,
    private val subjectService: SubjectService,
    private val lecturerDao: LecturerDao,
    private val examDao: ExamDao,
    private val homeworkDao: HomeworkDao
) : SubjectRepository {

    private suspend fun cacheSubject() {
        val subjects = subjectService.getAllSubject()
        subjectDao.getAllSubject().first().forEach { subject ->
            if (subjects.none { it.id == subject.id }) {
                subjectDao.deleteSubjectById(subject.id)
            }
        }
        subjectDao.upsertSubject(subjects)
    }

    override fun getAllSubject(): Flow<Result<Flow<List<Subject>>>> {
        return flow {
            emit(Result.Loading())
            cacheSubject()
            emit(Result.Success(subjectDao.getAllSubject()))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getSubjectWithLecturerById(id: String): Flow<Result<Flow<SubjectWithLecturer?>>> {
        return flow {
            emit(Result.Loading())
            val subjectWithLecturer = subjectService.getSubjectWithLecturerById(id)
            subjectDao.upsertSubject(subjectWithLecturer.subject)
            lecturerDao.upsertLecturer(subjectWithLecturer.lecturer)
            emit(Result.Success(subjectDao.getSubjectWithLecturerById(id)))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun saveSubject(subject: Subject): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            subjectService.saveSubject(subject)
            subjectDao.insertSubject(subject)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun onDeleteSubject(subject: Subject): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            subjectService.deleteSubjectById(subject.id)
            subjectDao.deleteSubjectById(subject.id) // Pastikan untuk menghapus dosen dari database
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun updateSubject(subject: Subject): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            subjectService.saveSubject(subject)
            subjectDao.updateSubject(subject)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getAllSubjectWithExam(): Flow<Result<Flow<List<SubjectWithExam>>>> {
        return flow {
            emit(Result.Loading())
            val subjectWithExam = subjectService.getAllSubjectWithExam()
            subjectDao.upsertSubject(subjectWithExam.map { it.subject })
            examDao.upsertExam(subjectWithExam.flatMap { it.exams })
            emit(Result.Success(subjectDao.getAllSubjectWithExam()))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getAllSubjectWithExamAndHomework(): Flow<Result<Flow<List<SubjectWithExamAndHomework>>>> {
        return flow {
            emit(Result.Loading())
            val subjectWithExamAndHomework = subjectService.getAllSubjectWithExamAndHomework()
            subjectDao.upsertSubject(subjectWithExamAndHomework.map { it.subject })
            examDao.upsertExam(subjectWithExamAndHomework.flatMap { it.exams })
            homeworkDao.upsertHomework(subjectWithExamAndHomework.flatMap { it.homeworks })
            emit(Result.Success(subjectDao.getSubjectWithExamAndHomework()))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getAllSubjectWithExamAndHomework(scored: Boolean): Flow<Result<Flow<List<SubjectWithExamAndHomework>>>> {
        return flow {
            emit(Result.Loading())
            val subjectWithExamAndHomework = subjectService.getAllSubjectWithExamAndHomework(scored)
            subjectDao.upsertSubject(subjectWithExamAndHomework.map { it.subject })
            examDao.upsertExam(subjectWithExamAndHomework.flatMap { it.exams })
            homeworkDao.upsertHomework(subjectWithExamAndHomework.flatMap { it.homeworks })
            emit(Result.Success(subjectDao.getSubjectWithExamAndHomework(scored)))
        }.catch {
            emit(Result.Error(it))
        }
    }
}