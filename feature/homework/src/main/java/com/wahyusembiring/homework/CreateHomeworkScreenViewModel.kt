package com.wahyusembiring.homework

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.scheduleReminder
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.repository.EventRepository
import com.wahyusembiring.data.repository.SubjectRepository
import com.wahyusembiring.ui.ReminderOption
import com.wahyusembiring.ui.util.ReminderType
import com.wahyusembiring.ui.util.UIText
import com.wahyusembiring.ui.util.toLocalDateTime
import com.wahyusembiring.ui.util.toReminderOption
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
import java.util.UUID

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
                is CreateHomeworkUIEvent.OnNotesChanged -> onNotesChanged(event.notes)
                is CreateHomeworkUIEvent.OnSaveHomeworkButtonClicked -> onSaveHomeworkButtonClick()
                is CreateHomeworkUIEvent.OnPickSubjectButtonClicked -> onSubjectPickerClick()
                is CreateHomeworkUIEvent.OnConfirmSaveHomeworkClick -> onConfirmSaveHomeworkClick()
                is CreateHomeworkUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)
                is CreateHomeworkUIEvent.OnSubjectPicked -> onSubjectSelected(event.subject)
                is CreateHomeworkUIEvent.OnHomeworkSavedButtonClicked -> onHomeworkSavedButtonClicked()
                is CreateHomeworkUIEvent.OnNavigateBackButtonClick -> onNavigateBackButtonClick()
                is CreateHomeworkUIEvent.OnNavigateToSubjectScreenRequest -> onNavigateToSubjectScreenRequest()
                is CreateHomeworkUIEvent.OnCustomDeadlineReminderButtonClicked -> onCustomDeadlineReminderButtonClicked()
                is CreateHomeworkUIEvent.OnCustomDueReminderButtonClicked -> onCustomDueReminderButtonClicked()
                is CreateHomeworkUIEvent.OnDeadlineDateChanged -> onDeadlineDateChanged(event.deadlineDate)
                is CreateHomeworkUIEvent.OnDeadlineReminderChanged -> onDeadlineReminderChanged(event.reminderOption)
                is CreateHomeworkUIEvent.OnDueDateChanged -> onDueDateChanged(event.dueDate)
                is CreateHomeworkUIEvent.OnDueReminderChanged -> onDueReminderChanged(event.reminderOption)
                is CreateHomeworkUIEvent.OnPickDuePeriodButtonClicked -> onPickDuePeriodButtonClicked()
                is CreateHomeworkUIEvent.OnTaskCompletedStatusChanged -> onTaskCompletedStatusChanged(event.isCompleted)
            }
        }
    }

    private fun onTaskCompletedStatusChanged(completed: Boolean) {
        viewModelScope.launch {
            eventRepository.updateCompletedStatus(homeworkId, completed).collect { result ->
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
                    }
                }
            }
        }
    }

    private fun onPickDuePeriodButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.DuePeriodPicker)
        }
    }

    private fun onDueReminderChanged(reminderOption: ReminderOption) {
        _state.update {
            it.copy(dueReminder = reminderOption)
        }
    }

    private fun onDueDateChanged(dueDate: LocalDateTime) {
        _state.update {
            it.copy(dueDate = dueDate)
        }
    }

    private fun onDeadlineReminderChanged(reminderOption: ReminderOption) {
        _state.update {
            it.copy(deadlineReminder = reminderOption)
        }
    }

    private fun onDeadlineDateChanged(deadlineDate: LocalDateTime) {
        _state.update {
            it.copy(deadline = deadlineDate)
        }
    }

    private fun onCustomDueReminderButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.DueReminderPicker)
        }
    }

    private fun onCustomDeadlineReminderButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.DeadlineReminderPicker)
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


    private suspend fun onSubjectPickerClick() {
        _state.update {
            it.copy(popUps = it.popUps + CreateHomeworkScreenPopUp.SubjectPicker)
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
                dueDate = _state.value.dueDate,
                deadline = _state.value.deadline,
                dueReminder = _state.value.dueReminder?.toLocalDateTime(_state.value.dueDate),
                deadlineReminder = _state.value.deadlineReminder?.toLocalDateTime(_state.value.deadline),
                subjectId = _state.value.subject?.id ?: throw MissingRequiredFieldException.Subject(),
                notes = _state.value.notes,
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
                        homework.dueReminder?.let { dueReminder ->
                            scheduleReminder(
                                context = application.applicationContext,
                                localDateTime = dueReminder,
                                title = homework.title,
                                reminderId = Pair(homework, dueReminder).hashCode()
                            )
                        }
                        homework.deadlineReminder?.let { deadlineReminder ->
                            scheduleReminder(
                                context = application.applicationContext,
                                localDateTime = deadlineReminder,
                                title = homework.title,
                                reminderId = Pair(homework, deadlineReminder).hashCode()
                            )
                        }
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
                        if (homework.dueReminder != null) {
                            scheduleReminder(
                                application.applicationContext,
                                homework.dueReminder!!,
                                homework.title,
                                homework.id.hashCode()
                            )
                        }
                        if (homework.deadlineReminder != null) {
                            scheduleReminder(
                                application.applicationContext,
                                homework.deadlineReminder!!,
                                homework.title,
                                homework.id.hashCode()
                            )
                        }
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
    }

    private fun onHomeworkTitleChanged(title: String) {
        _state.update {
            it.copy(homeworkTitle = title)
        }
    }

    private fun onNotesChanged(description: String) {
        _state.update {
            it.copy(notes = description)
        }
    }

    private fun onSubjectSelected(subject: Subject) {
        _state.update {
            it.copy(subject = subject)
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
                            result.data.collect { homeworkWithSubject ->
                                if (homeworkWithSubject == null) return@collect
                                _state.update {
                                    it.copy(
                                        isEditMode = true,
                                        homeworkTitle = homeworkWithSubject.homework.title,
                                        dueDate = homeworkWithSubject.homework.dueDate,
                                        dueReminder = with(homeworkWithSubject.homework) {
                                            dueReminder?.toReminderOption(dueDate, ReminderType.DUE)
                                        },
                                        deadline = homeworkWithSubject.homework.deadline,
                                        deadlineReminder = with(homeworkWithSubject.homework) {
                                            deadlineReminder?.toReminderOption(deadline, ReminderType.DEADLINE)
                                        },
                                        notes = homeworkWithSubject.homework.notes,
                                        subject = homeworkWithSubject.subject,
                                        isCompleted = homeworkWithSubject.homework.completed,
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