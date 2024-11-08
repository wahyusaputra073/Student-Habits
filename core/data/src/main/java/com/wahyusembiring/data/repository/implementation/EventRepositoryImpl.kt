package com.wahyusembiring.data.repository.implementation

import com.wahyusembiring.data.Result
import com.wahyusembiring.data.local.dao.ExamDao
import com.wahyusembiring.data.local.dao.HomeworkDao
import com.wahyusembiring.data.local.dao.ReminderDao
import com.wahyusembiring.data.local.dao.SubjectDao
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.remote.ExamService
import com.wahyusembiring.data.remote.HomeworkService
import com.wahyusembiring.data.remote.ReminderService
import com.wahyusembiring.data.repository.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val examDao: ExamDao,
    private val examService: ExamService,
    private val homeworkDao: HomeworkDao,
    private val homeworkService: HomeworkService,
    private val reminderDao: ReminderDao,
    private val reminderService: ReminderService,
    private val subjectDao: SubjectDao
) : EventRepository {

    private suspend fun cacheHomework() {
        val homeworkWithSubject = homeworkService.getAllHomeworkWithSubject()
        homeworkDao.deleteAllHomework()
        homeworkDao.insertHomework(homeworkWithSubject.map { it.homework })
        val subjects = homeworkWithSubject.map { it.subject }
        subjectDao.upsertSubject(subjects)
    }

    private suspend fun cacheExam() {
        val examWithSubject = examService.getAllExamWithSubject()
        examDao.deleteAllExam()
        examDao.insertExam(examWithSubject.map { it.exam })
        val subjects = examWithSubject.map { it.subject }
        subjectDao.upsertSubject(subjects)
    }

    private suspend fun cacheReminder() {
        val reminder = reminderService.getAllReminder()
        reminderDao.deleteAllReminder()
        reminderDao.insertReminder(reminder)
    }

    override fun getAllEvent(): Flow<Result<Flow<List<Any>>>> {
        return flow {
            emit(Result.Loading())
            try {
                // Cache data from cloud to local
                cacheHomework()
                cacheExam()
                cacheReminder()

                // Load data from local
                val homeworkFlow = homeworkDao.getAllHomeworkWithSubject()
                val examFlow = examDao.getAllExamWithSubject()
                val reminderFlow = reminderDao.getAllReminder()
                val combinedFlow = combine(homeworkFlow, examFlow, reminderFlow) { homework, exam, reminder ->
                    val events = mutableListOf<Any>()
                    events.addAll(homework)
                    events.addAll(exam)
                    events.addAll(reminder)
                    events
                }
                emit(Result.Success(combinedFlow))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun getHomeworkById(id: String): Flow<Result<Flow<HomeworkWithSubject?>>> {
        return flow {
            emit(Result.Loading())
            try {
                val homeworkWithSubject = homeworkService.getHomeworkWithSubjectById(id)
                homeworkDao.upsertHomework(homeworkWithSubject.homework)
                val subject = homeworkWithSubject.subject
                subjectDao.upsertSubject(subject)
                val homeworkFlow = homeworkDao.getHomeworkById(homeworkWithSubject.homework.id)
                emit(Result.Success(homeworkFlow))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun saveHomework(homework: Homework): Flow<Result<String>> {
        return flow {
            emit(Result.Loading())
            try {
                homeworkService.saveHomework(homework)
                homeworkDao.insertHomework(homework)
                emit(Result.Success(homework.id))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun updateHomework(homework: Homework): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            try {
                homeworkService.saveHomework(homework)
                homeworkDao.updateHomework(homework)
                emit(Result.Success(Unit))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun getAllExamWithSubject(): Flow<Result<Flow<List<ExamWithSubject>>>> {
        return flow {
            emit(Result.Loading())
            try {
                cacheExam()
                val examFlow = examDao.getAllExamWithSubject()
                emit(Result.Success(examFlow))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun saveExam(exam: Exam): Flow<Result<String>> {
        return flow {
            emit(Result.Loading())
            try {
                examService.saveExam(exam)
                examDao.insertExam(exam)
                emit(Result.Success(exam.id))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun updateExam(exam: Exam): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            try {
                examService.saveExam(exam)
                examDao.updateExam(exam)
                emit(Result.Success(Unit))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun saveReminder(reminder: Reminder): Flow<Result<String>> {
        return flow {
            emit(Result.Loading())
            try {
                reminderService.saveReminder(reminder)
                reminderDao.insertReminder(reminder)
                emit(Result.Success(reminder.id))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun updateReminder(reminder: Reminder): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            try {
                reminderService.saveReminder(reminder)
                reminderDao.updateReminder(reminder)
                emit(Result.Success(Unit))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun deleteExam(exam: Exam): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            try {
                examService.deleteExam(exam)
                examDao.deleteExam(exam)
                emit(Result.Success(Unit))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun deleteHomework(homework: Homework): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            try {
                homeworkService.deleteHomework(homework)
                homeworkDao.deleteHomework(homework)
                emit(Result.Success(Unit))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun deleteReminder(reminder: Reminder): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            try {
                reminderService.deleteReminder(reminder)
                reminderDao.deleteReminder(reminder)
                emit(Result.Success(Unit))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun getExamById(id: String): Flow<Result<Flow<ExamWithSubject?>>> {
        return flow {
            emit(Result.Loading())
            try {
                val examWithSubject = examService.getExamWithSubjectById(id)
                examDao.upsertExam(examWithSubject.exam)
                val subject = examWithSubject.subject
                subjectDao.upsertSubject(subject)
                val examFlow = examDao.getExamById(examWithSubject.exam.id)
                emit(Result.Success(examFlow))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }

    override fun getReminderById(id: String): Flow<Result<Flow<Reminder?>>> {
        return flow {
            emit(Result.Loading())
            try {
                val reminder = reminderService.getReminderById(id)
                reminderDao.upsertReminder(reminder)
                val reminderFlow = reminderDao.getReminderById(reminder.id)
                emit(Result.Success(reminderFlow))
            } catch (thr: Throwable) {
                emit(Result.Error(thr))
            }
        }
    }
}