package com.wahyusembiring.homework

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.scheduleReminder
import com.wahyusembiring.data.Result
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel(assistedFactory = CreateHomeworkScreenViewModel.Factory::class)
class CreateHomeworkScreenViewModel @AssistedInject constructor(
    @Assisted private val homeworkId: String = "-1",
    private val eventRepository: EventRepository,
    private val subjectRepository: SubjectRepository,
    private val application: Application
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(homeworkId: String = "-1"): CreateHomeworkScreenViewModel
    }

    private var getAllSubjectJob: Job? = null

    private val _state = MutableStateFlow(CreateHomeworkScreenUIState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<CreateHomeworkScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onUIEvent(event: CreateHomeworkUIEvent) {
        viewModelScope.launch {
            when (event) {
                is CreateHomeworkUIEvent.OnHomeworkTitleChanged -> onHomeworkTitleChanged(event.title)
                is CreateHomeworkUIEvent.OnExamDescriptionChanged -> onExamDescriptionChanged(event.title)
                is CreateHomeworkUIEvent.OnSaveHomeworkButtonClicked -> onSaveHomeworkButtonClick()
                is CreateHomeworkUIEvent.OnPickDateButtonClicked -> onDatePickerClick()
                is CreateHomeworkUIEvent.OnPickTimeButtonClicked -> onTimePickerClick()
                is CreateHomeworkUIEvent.OnPickSubjectButtonClicked -> onSubjectPickerClick()
                is CreateHomeworkUIEvent.OnPickAttachmentButtonClicked -> onAttachmentPickerClick()
                is CreateHomeworkUIEvent.OnAttachmentPicked -> onAttachmentPicked(event.attachments)
                is CreateHomeworkUIEvent.OnConfirmSaveHomeworkClick -> onConfirmSaveHomeworkClick()
                is CreateHomeworkUIEvent.OnDatePicked -> onDateSelected(event.date)
                is CreateHomeworkUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)
                is CreateHomeworkUIEvent.OnSubjectPicked -> onSubjectSelected(event.subject)
                is CreateHomeworkUIEvent.OnTimePicked -> onTimeSelected(event.time)
                is CreateHomeworkUIEvent.OnHomeworkSavedButtonClicked -> onHomeworkSavedButtonClicked()
                CreateHomeworkUIEvent.OnNavigateBackButtonClick -> onNavigateBackButtonClick()
                CreateHomeworkUIEvent.OnNavigateToSubjectScreenRequest -> onNavigateToSubjectScreenRequest()
            }
        }
    }

    private fun onNavigateToSubjectScreenRequest() {
        _navigationEvent.trySend(CreateHomeworkScreenNavigationEvent.NavigateToCreateSubject)
    }

    private fun onNavigateBackButtonClick() {
        _navigationEvent.trySend(CreateHomeworkScreenNavigationEvent.NavigateBack)
    }

    private fun onHomeworkSavedButtonClicked() {
        _navigationEvent.trySend(CreateHomeworkScreenNavigationEvent.NavigateBack)
    }

    private fun onDismissPopUp(popUp: CreateHomeworkScreenPopUp) {
        _state.update {
            it.copy(popUps = it.popUps - popUp)
        }
    }


    private fun onAttachmentPicked(attachments: List<Attachment>) {
        _state.update {
            it.copy(attachments = attachments)
        }
    }

    private suspend fun onAttachmentPickerClick() {
        _state.update {
            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.AttachmentPicker)
        }
    }

    private suspend fun onSubjectPickerClick() {
        _state.update {
            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.SubjectPicker)
        }
    }

    private suspend fun onTimePickerClick() {
        _state.update {
            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.TimePicker)
        }
    }

    private suspend fun onDatePickerClick() {
        _state.update {
            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.DatePicker)
        }
    }

    private suspend fun onSaveHomeworkButtonClick() {
        _state.update {
            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.SaveConfirmationDialog)
        }
    }

    private fun onConfirmSaveHomeworkClick() {
        try {
            val homework = Homework(
                id = if (homeworkId == "-1") UUID.randomUUID().toString() else homeworkId,
                title = _state.value.homeworkTitle.ifBlank { throw MissingRequiredFieldException.Title() },
                dueDate = _state.value.date ?: throw MissingRequiredFieldException.Date(),
                subjectId = _state.value.subject?.id ?: throw MissingRequiredFieldException.Subject(),
                reminder = _state.value.time,
                description = _state.value.description,
                attachments = _state.value.attachments,
                completed = _state.value.isCompleted
            )

            viewModelScope.launch {
                if (homeworkId == "-1") {
                    saveHomework(homework)
                } else {
                    updateHomework(homework)
                }
            }
        } catch (e: MissingRequiredFieldException) {
            val errorMessage = when (e) {
                is MissingRequiredFieldException.Title -> UIText.StringResource(R.string.homework_title_is_required)
                is MissingRequiredFieldException.Date -> UIText.StringResource(R.string.due_date_is_required)
                is MissingRequiredFieldException.Subject -> UIText.StringResource(R.string.subject_is_required)
            }
            _state.update {
                it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.Error(errorMessage))
            }
        }
    }

    private suspend fun updateHomework(homework: Homework) {
        eventRepository.updateHomework(homework)
            .collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.Loading)
                        }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CreateHomeworkScreenPopUp.Loading)
                                    .plus(CreateHomeworkScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown error")))
                            )
                        }
                    }
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CreateHomeworkScreenPopUp.Loading)
                                    .plus(CreateHomeworkScreenPopUp.HomeworkSavedDialog)
                            )
                        }
                    }
                }
            }

        if (homework.reminder != null) {
            scheduleReminder(
                context = application.applicationContext,
                localDateTime = LocalDateTime.of(
                    homework.dueDate,
                    homework.reminder
                ),
                title = homework.title,
                reminderId = homework.id.hashCode()
            )
        }
    }

    private suspend fun saveHomework(homework: Homework) {
        eventRepository.saveHomework(homework)
            .collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.Loading)
                        }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CreateHomeworkScreenPopUp.Loading)
                                    .plus(CreateHomeworkScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown error")))
                            )
                        }
                    }
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CreateHomeworkScreenPopUp.Loading)
                                    .plus(CreateHomeworkScreenPopUp.HomeworkSavedDialog)
                            )
                        }
                    }
                }
            }

        if (homework.reminder != null) {
            scheduleReminder(
                context = application.applicationContext,
                localDateTime = LocalDateTime.of(
                    homework.dueDate,
                    homework.reminder
                ),
                title = homework.title,
                reminderId = homework.id.hashCode()
            )
        }
    }

    private fun onHomeworkTitleChanged(title: String) {
        _state.update {
            it.copy(homeworkTitle = title)
        }
    }

    private fun onExamDescriptionChanged(description: String) {
        _state.update {
            it.copy(description = description)
        }
    }

    private fun onDateSelected(date: LocalDate) {
        _state.update {
            it.copy(date = date)
        }
    }

    private fun onTimeSelected(time: LocalTime) {
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
        if (homeworkId != "-1") {
            viewModelScope.launch {
                eventRepository.getHomeworkById(homeworkId).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _state.update {
                                it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.Loading)
                            }
                        }
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    popUps = it.popUps
                                        .minus(CreateHomeworkScreenPopUp.Loading)
                                        .plus(CreateHomeworkScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown error")))
                                )
                            }
                        }
                        is Result.Success -> {
                            _state.update {
                                it.copy(popUps = it.popUps.minus(CreateHomeworkScreenPopUp.Loading))
                            }
                            result.data.collect { homeworkDto ->
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
                                        description = homeworkDto.homework.description,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        getAllSubjectJob = viewModelScope.launch {
            subjectRepository.getAllSubject().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.Loading)
                        }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CreateHomeworkScreenPopUp.Loading)
                                    .plus(CreateHomeworkScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown error")))
                            )
                        }
                    }
                    is Result.Success -> {
                        _state.update {
                            it.copy(popUps = it.popUps.minus(CreateHomeworkScreenPopUp.Loading))
                        }
                        result.data.collect { subjects ->
                            _state.update {
                                it.copy(subjects = subjects)
                            }
                        }
                    }
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