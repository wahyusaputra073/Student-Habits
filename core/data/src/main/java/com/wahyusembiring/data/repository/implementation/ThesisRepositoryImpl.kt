package com.wahyusembiring.data.repository.implementation

import com.wahyusembiring.data.Result
import com.wahyusembiring.data.local.dao.TaskDao
import com.wahyusembiring.data.local.dao.ThesisDao
import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.data.model.entity.Thesis
import com.wahyusembiring.data.model.ThesisWithTask
import com.wahyusembiring.data.remote.ThesisService
import com.wahyusembiring.data.repository.ThesisRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ThesisRepositoryImpl @Inject constructor(
    private val thesisDao: ThesisDao,
    private val thesisService: ThesisService,
    private val taskDao: TaskDao,
) : ThesisRepository {

    override fun getAllThesis(): Flow<Result<Flow<List<ThesisWithTask>>>> {
        return flow {
            emit(Result.Loading())
            val thesisWithTask = thesisService.getAllThesisWithTask()
            thesisDao.getAllThesis().first().forEach { thesis ->
                if (thesisWithTask.none { it.thesis.id == thesis.thesis.id }) {
                    thesisDao.deleteThesis(thesis.thesis)
                }
            }
            taskDao.getAllTasks().first().forEach { task ->
                if (thesisWithTask.flatMap { it.tasks }.none { it.id == task.id }) {
                    taskDao.deleteTask(task)
                }
            }
            thesisDao.upsertThesis(thesisWithTask.map { it.thesis })
            taskDao.upsertTask(thesisWithTask.flatMap { it.tasks }.distinctBy { it.id })
            emit(Result.Success(thesisDao.getAllThesis()))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun getThesisById(id: String): Flow<Result<Flow<ThesisWithTask>>> {
        return flow {
            emit(Result.Loading())
            val thesisWithTask = thesisService.getThesisById(id)
            thesisDao.updateThesis(thesisWithTask.thesis)
            taskDao.upsertTask(thesisWithTask.tasks)
            emit(Result.Success(thesisDao.getThesisById(id)))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun saveNewThesis(thesis: Thesis): Flow<Result<String>> {
        return flow {
            emit(Result.Loading())
            thesisService.saveNewThesis(thesis)
            thesisDao.insertThesis(thesis)
            emit(Result.Success(thesis.id))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun updateThesis(thesis: Thesis): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            thesisService.saveNewThesis(thesis)
            thesisDao.updateThesis(thesis)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun updateThesisTitleById(id: String, title: String): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            thesisService.updateThesisTitleById(id, title)
            thesisDao.updateThesisTitleById(id, title)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun deleteThesis(thesis: Thesis): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            thesisService.deleteThesis(thesis)
            thesisDao.deleteThesis(thesis)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun addNewTask(task: Task): Flow<Result<String>> {
        return flow<Result<String>> {
            emit(Result.Loading())
            thesisService.addNewTask(task)
            taskDao.insertTask(task)
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun deleteTask(task: Task): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            thesisService.deleteTask(task)
            taskDao.deleteTask(task)
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

    override fun changeTaskCompletedStatus(task: Task, isCompleted: Boolean): Flow<Result<Unit>> {
        return flow {
            emit(Result.Loading())
            supervisorScope {
                launch(Dispatchers.IO) {
                    thesisService.changeTaskCompletedStatus(task, isCompleted)
                }
            }
            taskDao.updateTask(task.copy(isCompleted = isCompleted))
            emit(Result.Success(Unit))
        }.catch {
            emit(Result.Error(it))
        }
    }

}