package com.wahyusembiring.data.repository.implementation

import com.wahyusembiring.data.local.dao.ExamDao
import com.wahyusembiring.data.local.dao.HomeworkDao
import com.wahyusembiring.data.local.dao.LecturerDao
import com.wahyusembiring.data.local.dao.ReminderDao
import com.wahyusembiring.data.local.dao.SubjectDao
import com.wahyusembiring.data.local.dao.TaskDao
import com.wahyusembiring.data.local.dao.ThesisDao
import com.wahyusembiring.data.remote.ExamService
import com.wahyusembiring.data.remote.HomeworkService
import com.wahyusembiring.data.remote.LecturerService
import com.wahyusembiring.data.remote.ReminderService
import com.wahyusembiring.data.remote.SubjectService
import com.wahyusembiring.data.remote.ThesisService
import com.wahyusembiring.data.repository.MainRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.wahyusembiring.data.Result

class MainRepositoryImpl @Inject constructor(
    private val examService: ExamService,
    private val examDao: ExamDao,
    private val homeworkService: HomeworkService,
    private val homeworkDao: HomeworkDao,
    private val reminderService: ReminderService,
    private val reminderDao: ReminderDao,
    private val subjectService: SubjectService,
    private val subjectDao: SubjectDao,
    private val thesisService: ThesisService,
    private val thesisDao: ThesisDao,
    private val taskDao: TaskDao,
    private val lecturerService: LecturerService,
    private val lecturerDao: LecturerDao
) : MainRepository {

    override suspend fun syncToLocal(): Flow<Result<Unit>> = flow {
        emit(Result.Loading())
        try {
            val lecturers = lecturerService.getAllLecturer()
            lecturerDao.insertLecturer(lecturers)

            val subjects = subjectService.getAllSubject()
            subjectDao.insertSubject(subjects)

            val homeworks = homeworkService.getAllHomework()
            homeworkDao.insertHomework(homeworks)

            val exams = examService.getAllExam()
            examDao.insertExam(exams)

            val reminders = reminderService.getAllReminder()
            reminderDao.insertReminder(reminders)

            val thesisWithTask = thesisService.getAllThesisWithTask()
            thesisDao.insertThesis(thesisWithTask.map { it.thesis })
            taskDao.insertTask(thesisWithTask.flatMap { it.tasks })
            emit(Result.Success(Unit))
        } catch (thr: Throwable) {
            emit(Result.Error(thr))
        }
    }

    override suspend fun syncToCloud(): Flow<Result<Unit>> = flow {
        emit(Result.Loading())
        try {
            val lecturers = lecturerDao.getAllLecturer().first()
            lecturers.forEach { lecturerService.saveLecturer(it) }

            val subjects = subjectDao.getAllSubject().first()
            subjects.forEach { subjectService.saveSubject(it) }

            val homeworks = homeworkDao.getAllHomework().first()
            homeworks.forEach { homeworkService.saveHomework(it) }

            val exams = examDao.getAllExam().first()
            exams.forEach { examService.saveExam(it) }

            val reminders = reminderDao.getAllReminder().first()
            reminders.forEach { reminderService.saveReminder(it) }

            val thesisWithTask = thesisDao.getAllThesis().first()
            thesisWithTask.forEach { thesisService.saveThesisWithTask(it) }

            emit(Result.Success(Unit))
        } catch (thr: Throwable) {
            emit(Result.Error(thr))
        }
    }

}