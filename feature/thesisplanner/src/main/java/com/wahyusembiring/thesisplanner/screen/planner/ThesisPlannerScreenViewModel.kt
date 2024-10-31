package com.wahyusembiring.thesisplanner.screen.planner

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.launch
import com.wahyusembiring.data.model.File
import com.wahyusembiring.data.model.ThesisWithTask
import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.data.model.entity.Thesis
import com.wahyusembiring.data.repository.ThesisRepository
import com.wahyusembiring.ui.util.UIText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.wahyusembiring.thesisplanner.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = ThesisPlannerScreenViewModel.Factory::class)
class ThesisPlannerScreenViewModel @AssistedInject constructor(
    @Assisted private val thesisId: Int,
    private val thesisRepository: ThesisRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(thesisId: Int): ThesisPlannerScreenViewModel
    }

    private val debounceDuration = 1000L
    private var savingTitleJob: Job? = null
    private lateinit var thesis: ThesisWithTask

    init {
        viewModelScope.launch {
            thesisRepository.getThesisById(thesisId).collect { thesis ->
                this@ThesisPlannerScreenViewModel.thesis = thesis
                _uiState.update {
                    it.copy(
                        thesisTitle = thesis.thesis.title,
                        editedThesisTitle = thesis.thesis.title,
                        articles = thesis.thesis.articles,
                        tasks = thesis.tasks
                    )
                }
            }
        }
    }

    private val _uiState = MutableStateFlow(ThesisPlannerScreenUIState())
    val uiState: StateFlow<ThesisPlannerScreenUIState> = _uiState.asStateFlow()

    fun onUIEvent(event: ThesisPlannerScreenUIEvent) {
        when (event) {
            is ThesisPlannerScreenUIEvent.OnArticleClick -> onArticleClick(event.article)
            is ThesisPlannerScreenUIEvent.OnDeleteArticleClick -> launch {
                onDeleteArticleClick(
                    event.article
                )
            }

            is ThesisPlannerScreenUIEvent.OnDocumentPickerResult -> onDocumentPickerResult(event.files)
            is ThesisPlannerScreenUIEvent.OnThesisTitleChange -> onThesisTitleChange(event.thesisName)
            is ThesisPlannerScreenUIEvent.OnSaveTaskClick -> launch { onSaveTaskClick(event.task) }
            is ThesisPlannerScreenUIEvent.OnTaskCompletedStatusChange -> launch {
                onTaskCompletedStatusChange(
                    event.task,
                    event.isCompleted
                )
            }

            is ThesisPlannerScreenUIEvent.OnArticleDeleteDialogDismiss -> onArticleDeleteDialogDismiss()
            is ThesisPlannerScreenUIEvent.OnCreateTaskButtonClick -> onCreateTaskButtonClick()
            is ThesisPlannerScreenUIEvent.OnCreateTaskDialogDismiss -> onCreateTaskDialogDismiss()
            is ThesisPlannerScreenUIEvent.OnDatePickerButtonClick -> onDatePickerButtonClick()
            is ThesisPlannerScreenUIEvent.OnDatePickerDismiss -> onDatePickerDismiss()
            is ThesisPlannerScreenUIEvent.OnDeleteArticleConfirm -> onDeleteArticleConfirm(event.article)
            is ThesisPlannerScreenUIEvent.OnDeleteTaskClick -> onDeleteTaskClick(event.task)
            is ThesisPlannerScreenUIEvent.OnTaskDeleteConfirm -> onTaskDeleteConfirm(event.task)
            is ThesisPlannerScreenUIEvent.OnTaskDeleteDialogDismiss -> onTaskDeleteDialogDismiss()
        }
    }

    private fun onTaskDeleteDialogDismiss() {
        _uiState.update { it.copy(taskPendingDelete = null) }
    }

    private fun onTaskDeleteConfirm(task: Task) {
        viewModelScope.launch {
            thesisRepository.deleteTask(task)
        }
    }

    private fun onDeleteTaskClick(task: Task) {
        _uiState.update {
            it.copy(taskPendingDelete = task)
        }
    }

    private fun onDeleteArticleConfirm(article: File) {
        viewModelScope.launch {
            thesisRepository.updateThesis(
                thesis.thesis.let {
                    it.copy(articles = it.articles - article)
                }
            )
        }
    }

    private fun onDatePickerDismiss() {
        _uiState.update { it.copy(showDatePicker = false) }
    }

    private fun onDatePickerButtonClick() {
        _uiState.update { it.copy(showDatePicker = true) }
    }

    private fun onCreateTaskDialogDismiss() {
        _uiState.update { it.copy(showCreateTaskDialog = false) }
    }

    private fun onCreateTaskButtonClick() {
        _uiState.update { it.copy(showCreateTaskDialog = true) }
    }

    private fun onArticleDeleteDialogDismiss() {
        _uiState.update { it.copy(articlePendingDelete = null) }
    }

    private fun onArticleClick(article: File) {

    }

    private suspend fun onTaskCompletedStatusChange(task: Task, completed: Boolean) {
        thesisRepository.changeTaskCompletedStatus(task, completed)
    }

    private suspend fun onSaveTaskClick(task: Task) {
        val updatedTask = task.copy(thesisId = thesisId)
        thesisRepository.addNewTask(updatedTask)
    }

    private fun onThesisTitleChange(thesisName: String) {
        _uiState.update {
            it.copy(
                editedThesisTitle = thesisName
            )
        }
        savingTitleJob = viewModelScope.launch {
            if (savingTitleJob?.isActive == true) savingTitleJob?.cancel()
            delay(debounceDuration)
            thesisRepository.updateThesisTitleById(thesisId, thesisName)
        }
    }


    private fun onDeleteArticleClick(article: File) {
        _uiState.update {
            it.copy(articlePendingDelete = article)
        }
    }

    private fun onDocumentPickerResult(articles: List<File>) {
        viewModelScope.launch {
            articles.forEach {
                thesisRepository.updateThesis(
                    thesis = thesis.thesis.copy(articles = thesis.thesis.articles + it)
                )
            }
        }
    }


}