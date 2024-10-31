package com.wahyusembiring.data.repository.implementation

import android.database.sqlite.SQLiteConstraintException
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
import com.wahyusembiring.data.remote.SubjectService
import com.wahyusembiring.data.repository.EventRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepositoryImpl @Inject constructor(
    private val examDao: ExamDao,
    private val homeworkDao: HomeworkDao,
    private val reminderDao: ReminderDao,
) : EventRepository {


    override fun getAllEvent(): Flow<List<Any>> {
        val homeworkFlow = homeworkDao.getAllHomeworkWithSubject()
        val examFlow = examDao.getAllExamWithSubject()
        val reminderFlow = reminderDao.getAllReminder()
        return combine(homeworkFlow, examFlow, reminderFlow) { homework, exam, reminder ->
            val events = mutableListOf<Any>()
            events.addAll(homework)
            events.addAll(exam)
            events.addAll(reminder)
            events
        }
    }

    override fun getAllHomeworkWithSubject(
        minDate: Long?,
        maxDate: Long?
    ): Flow<List<HomeworkWithSubject>> {
        return if (minDate == null || maxDate == null) {
            homeworkDao.getAllHomeworkWithSubject()
        } else {
            homeworkDao.getAllHomeworkWithSubject(minDate, maxDate)
        }
    }

    override fun getHomeworkById(id: Int): Flow<HomeworkWithSubject?> {
        return homeworkDao.getHomeworkById(id)
    }

    override suspend fun saveHomework(homework: Homework): Long {
        return homeworkDao.insertHomework(homework)
    }

    override suspend fun updateHomework(homework: Homework) {
        homeworkDao.updateHomework(homework)
    }

    override fun getAllExamWithSubject(): Flow<List<ExamWithSubject>> {
        return examDao.getAllExamWithSubject()
    }

    override suspend fun saveExam(exam: Exam): Long {
        return examDao.insertExam(exam)
    }

    override suspend fun updateExam(exam: Exam) {
        examDao.updateExam(exam)
    }

    override fun getAllReminder(minDate: Long?, maxDate: Long?): Flow<List<Reminder>> {
        return if (minDate == null || maxDate == null) {
            reminderDao.getAllReminder()
        } else {
            reminderDao.getAllReminder(minDate, maxDate)
        }
    }

    override suspend fun saveReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }

    override suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }

    override suspend fun deleteExam(exam: Exam) {
        examDao.deleteExam(exam)
    }

    override suspend fun deleteHomework(homework: Homework) {
        homeworkDao.deleteHomework(homework)
    }

    override suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }

    override fun getExamById(id: Int): Flow<ExamWithSubject?> {
        return examDao.getExamById(id)
    }

    override fun getReminderById(id: Int): Flow<Reminder?> {
        return reminderDao.getReminderById(id)
    }
}