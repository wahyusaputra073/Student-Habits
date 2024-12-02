package com.wahyusembiring.data.repository.implementation

import android.util.Log
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
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
        homeworkDao.getAllHomework().first().forEach { homework ->
            if (homeworkWithSubject.none { it.homework.id == homework.id }) {
                homeworkDao.deleteHomework(homework)
            }
        }
        homeworkDao.upsertHomework(homeworkWithSubject.map { it.homework })
        val subjects = homeworkWithSubject.map { it.subject }
        subjectDao.upsertSubject(subjects)
    }

    private suspend fun cacheExam() {
        val examWithSubject = examService.getAllExamWithSubject()
        examDao.getAllExam().first().forEach { exam ->
            if (examWithSubject.none { it.exam.id == exam.id }) {
                examDao.deleteExam(exam)
            }
        }
        examDao.upsertExam(examWithSubject.map { it.exam })
        val subjects = examWithSubject.map { it.subject }
        subjectDao.upsertSubject(subjects)
    }

    private suspend fun cacheReminder() {
        val reminders = reminderService.getAllReminder()
        reminderDao.getAllReminder().first().forEach { reminder ->
            if (reminders.none { it.id == reminder.id }) {
                reminderDao.deleteReminder(reminder)
            }
        }
        reminderDao.upsertReminder(reminders)
    }

    override fun getAllEvent(): Flow<Result<Flow<List<Any>>>> {
        return flow<Result<Flow<List<Any>>>> {
            emit(Result.Loading())
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
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getHomeworkById(id: String): Flow<Result<Flow<HomeworkWithSubject?>>> {
        return flow<Result<Flow<HomeworkWithSubject?>>> {
            emit(Result.Loading())
            val homeworkWithSubject = homeworkService.getHomeworkWithSubjectById(id)
            homeworkDao.upsertHomework(homeworkWithSubject.homework)
            val subject = homeworkWithSubject.subject
            subjectDao.upsertSubject(subject)
            val homeworkFlow = homeworkDao.getHomeworkById(homeworkWithSubject.homework.id)
            emit(Result.Success(homeworkFlow))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun saveHomework(homework: Homework): Flow<Result<String>> {
        return flow<Result<String>> {
            emit(Result.Loading())
            homeworkService.saveHomework(homework)
            homeworkDao.insertHomework(homework)
            emit(Result.Success(homework.id))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun updateHomework(homework: Homework): Flow<Result<Unit>> {
        return flow<Result<Unit>> {
            emit(Result.Loading())
            homeworkService.saveHomework(homework)
            homeworkDao.updateHomework(homework)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun updateCompletedStatus(
        homeworkId: String,
        isCompleted: Boolean
    ): Flow<Result<Unit>> {
        return flow<Result<Unit>> {
            emit(Result.Loading())
            homeworkService.updateCompletedStatus(homeworkId, isCompleted)
            homeworkDao.updateCompletedStatus(homeworkId, isCompleted)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getAllExamWithSubject(): Flow<Result<Flow<List<ExamWithSubject>>>> {
        return flow<Result<Flow<List<ExamWithSubject>>>> {
            emit(Result.Loading())
            cacheExam()
            val examFlow = examDao.getAllExamWithSubject()
            emit(Result.Success(examFlow))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun saveExam(exam: Exam): Flow<Result<String>> {
        return flow<Result<String>> {
            emit(Result.Loading())
            examService.saveExam(exam)
            examDao.insertExam(exam)
            emit(Result.Success(exam.id))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun updateExam(exam: Exam): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            examService.saveExam(exam)
            examDao.updateExam(exam)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun updateExamScore(examId: String, score: Int?): Flow<Result<Unit>> {
        return flow<Result<Unit>> {
            emit(Result.Loading())
            examService.updateExamScore(examId, score)
            examDao.updateExamScore(examId, score)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun saveReminder(reminder: Reminder): Flow<Result<String>> {
        return flow {
            emit(Result.Loading())
            reminderService.saveReminder(reminder)
            reminderDao.insertReminder(reminder)
            emit(Result.Success(reminder.id))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun updateReminder(reminder: Reminder): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            reminderService.saveReminder(reminder)
            reminderDao.updateReminder(reminder)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun deleteExam(exam: Exam): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            examService.deleteExam(exam)
            examDao.deleteExam(exam)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun deleteHomework(homework: Homework): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            homeworkService.deleteHomework(homework)
            homeworkDao.deleteHomework(homework)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun deleteReminder(reminder: Reminder): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            reminderService.deleteReminder(reminder)
            reminderDao.deleteReminder(reminder)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getExamById(id: String): Flow<Result<Flow<ExamWithSubject?>>> {
        return flow {
            emit(Result.Loading())
            val examWithSubject = examService.getExamWithSubjectById(id)
            examDao.upsertExam(examWithSubject.exam)
            val subject = examWithSubject.subject
            subjectDao.upsertSubject(subject)
            val examFlow = examDao.getExamById(examWithSubject.exam.id)
            emit(Result.Success(examFlow))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getReminderById(id: String): Flow<Result<Flow<Reminder?>>> {
        return flow {
            emit(Result.Loading())
            val reminder = reminderService.getReminderById(id)
            reminderDao.upsertReminder(reminder)
            val reminderFlow = reminderDao.getReminderById(reminder.id)
            emit(Result.Success(reminderFlow))
        }.catch {
            emit(Result.Error(it))
        }
    }
}