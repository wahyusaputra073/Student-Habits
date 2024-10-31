package com.wahyusembiring.homework

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.scheduleReminder
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.repository.EventRepository
import com.wahyusembiring.data.repository.HomeworkRepository
import com.wahyusembiring.data.repository.SubjectRepository
import com.wahyusembiring.ui.util.UIText
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@HiltViewModel(assistedFactory = CreateHomeworkScreenViewModel.Factory::class)
class CreateHomeworkScreenViewModel @AssistedInject constructor(
    @Assisted private val homeworkId: Int = -1,
    private val eventRepository: EventRepository,
    private val subjectRepository: SubjectRepository,
    private val application: Application
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(homeworkId: Int = -1): CreateHomeworkScreenViewModel
    }

    private var getAllSubjectJob: Job? = null

    private val _state = MutableStateFlow(CreateHomeworkScreenUIState())
    val state = _state.asStateFlow()

    fun onUIEvent(event: CreateHomeworkUIEvent) {
        viewModelScope.launch {
            when (event) {
                is CreateHomeworkUIEvent.OnHomeworkTitleChanged -> onHomeworkTitleChanged(event.title)
                is CreateHomeworkUIEvent.OnSaveHomeworkButtonClicked -> onSaveHomeworkButtonClick()
                is CreateHomeworkUIEvent.OnPickDateButtonClicked -> onDatePickerClick()
                is CreateHomeworkUIEvent.OnPickTimeButtonClicked -> onTimePickerClick()
                is CreateHomeworkUIEvent.OnPickSubjectButtonClicked -> onSubjectPickerClick()
                is CreateHomeworkUIEvent.OnPickAttachmentButtonClicked -> onAttachmentPickerClick()
                is CreateHomeworkUIEvent.OnAttachmentPicked -> onAttachmentPicked(event.attachments)
                is CreateHomeworkUIEvent.OnConfirmSaveHomeworkClick -> onConfirmSaveHomeworkClick()
                is CreateHomeworkUIEvent.OnDatePicked -> onDateSelected(event.date)
                is CreateHomeworkUIEvent.OnDismissAttachmentPicker -> onDismissAttachmentPicker()
                is CreateHomeworkUIEvent.OnDismissDatePicker -> onDismissDatePicker()
                is CreateHomeworkUIEvent.OnDismissHomeworkSavedDialog -> onDismissHomeworkSavedDialog()
                is CreateHomeworkUIEvent.OnDismissSaveConfirmationDialog -> onDismissSaveConfirmationDialog()
                is CreateHomeworkUIEvent.OnDismissSubjectPicker -> onDismissSubjectPicker()
                is CreateHomeworkUIEvent.OnDismissTimePicker -> onDismissTimePicker()
                is CreateHomeworkUIEvent.OnSubjectPicked -> onSubjectSelected(event.subject)
                is CreateHomeworkUIEvent.OnTimePicked -> onTimeSelected(event.time)
                is CreateHomeworkUIEvent.OnDismissErrorDialog -> onDismissErrorDialog()
                is CreateHomeworkUIEvent.OnDismissSavingLoading -> onDismissSavingLoading()
            }
        }
    }

    private fun onDismissSavingLoading() {
        _state.update {
            it.copy(showSavingLoading = false)
        }
    }

    private fun onDismissErrorDialog() {
        _state.update {
            it.copy(errorMessage = null)
        }
    }

    private fun onDismissTimePicker() {
        _state.update {
            it.copy(showTimePicker = false)
        }
    }

    private fun onDismissSubjectPicker() {
        _state.update {
            it.copy(showSubjectPicker = false)
        }
    }

    private fun onDismissSaveConfirmationDialog() {
        _state.update {
            it.copy(showSaveConfirmationDialog = false)
        }
    }

    private fun onDismissHomeworkSavedDialog() {
        _state.update {
            it.copy(showHomeworkSavedDialog = false)
        }
    }

    private fun onDismissDatePicker() {
        _state.update {
            it.copy(showDatePicker = false)
        }
    }

    private fun onDismissAttachmentPicker() {
        _state.update {
            it.copy(showAttachmentPicker = false)
        }
    }

    private fun onAttachmentPicked(attachments: List<Attachment>) {
        _state.update {
            it.copy(attachments = attachments)
        }
    }

    private suspend fun onAttachmentPickerClick() {
        _state.update {
            it.copy(showAttachmentPicker = true)
        }
    }

    private suspend fun onSubjectPickerClick() {
        _state.update {
            it.copy(showSubjectPicker = true)
        }
    }

    private suspend fun onTimePickerClick() {
        _state.update {
            it.copy(showTimePicker = true)
        }
    }

    private suspend fun onDatePickerClick() {
        _state.update {
            it.copy(showDatePicker = true)
        }
    }

    private suspend fun onSaveHomeworkButtonClick() {
        _state.update {
            it.copy(showSaveConfirmationDialog = true)
        }
    }

    private fun onConfirmSaveHomeworkClick() {
        _state.update { it.copy(showSavingLoading = true) }
        try {
            viewModelScope.launch {
                saveHomework()
                _state.update {
                    it.copy(showSavingLoading = false, showHomeworkSavedDialog = true)
                }
            }
        } catch (e: MissingRequiredFieldException) {
            _state.update { it.copy(showSavingLoading = false) }
            val errorMessage = when (e) {
                is MissingRequiredFieldException.Title -> UIText.StringResource(R.string.homework_title_is_required)
                is MissingRequiredFieldException.Date -> UIText.StringResource(R.string.due_date_is_required)
                is MissingRequiredFieldException.Subject -> UIText.StringResource(R.string.subject_is_required)
            }
            _state.update { it.copy(errorMessage = errorMessage) }
        }
    }

    private suspend fun saveHomework() {
        val homework = Homework(
            id = if (homeworkId == -1) 0 else homeworkId,
            title = _state.value.homeworkTitle.ifBlank { throw MissingRequiredFieldException.Title() },
            dueDate = _state.value.date ?: throw MissingRequiredFieldException.Date(),
            subjectId = _state.value.subject?.id ?: throw MissingRequiredFieldException.Subject(),
            reminder = _state.value.time,
            description = _state.value.description,
            attachments = _state.value.attachments,
            completed = _state.value.isCompleted
        )
        val newHomeworkId = if (homeworkId == -1) {
            eventRepository.saveHomework(homework)
        } else {
            eventRepository.updateHomework(homework)
            homeworkId
        }
        if (homework.reminder != null) {
            scheduleReminder(
                context = application.applicationContext,
                localDateTime = LocalDateTime.of(
                    LocalDate.ofInstant(homework.dueDate.toInstant(), ZoneId.systemDefault()),
                    LocalTime.of(homework.reminder!!.hour, homework.reminder!!.minute)
                ),
                title = homework.title,
                reminderId = newHomeworkId.toInt()
            )
        }
    }

    private fun onHomeworkTitleChanged(title: String) {
        _state.update {
            it.copy(homeworkTitle = title)
        }
    }

    private fun onDateSelected(date: Date) {
        _state.update {
            it.copy(date = date)
        }
    }

    private fun onTimeSelected(time: Time) {
        _state.update {
            it.copy(time = time)
        }
    }

    private fun onSubjectSelected(subject: Subject) {
        _state.update {
            it.copy(subject = subject)
        }
    }

    private fun onAttachmentsConfirmed(attachments: List<Attachment>) {
        _state.update {
            it.copy(attachments = attachments)
        }
    }

    init {
        if (homeworkId != -1) {
            viewModelScope.launch {
                eventRepository.getHomeworkById(homeworkId).collect { homeworkDto ->
                    if (homeworkDto == null) return@collect
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            homeworkTitle = homeworkDto.homework.title,
                            date = homeworkDto.homework.dueDate,
                            time = homeworkDto.homework.reminder,
                            subject = homeworkDto.subject,
                            attachments = homeworkDto.homework.attachments,
                            isCompleted = homeworkDto.homework.completed,
                        )
                    }
                }
            }
        }
        getAllSubjectJob = viewModelScope.launch {
            subjectRepository.getAllSubject().collect { subjects ->
                _state.update {
                    it.copy(subjects = subjects)
                }
            }
        }
    }

    override fun onCleared() {
        getAllSubjectJob?.cancel()
        getAllSubjectJob = null
        super.onCleared()
    }

}