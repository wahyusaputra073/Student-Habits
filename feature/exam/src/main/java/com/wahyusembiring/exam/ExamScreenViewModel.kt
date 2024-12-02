package com.wahyusembiring.exam

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.launch
import com.wahyusembiring.common.util.scheduleReminder
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.ExamCategory
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

@HiltViewModel(assistedFactory = ExamScreenViewModel.Factory::class)
class ExamScreenViewModel @AssistedInject constructor(
    @Assisted val examId: String = "-1",
    private val eventRepository: EventRepository,
    private val subjectRepository: SubjectRepository,
    private val application: Application
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(examId: String = "-1"): ExamScreenViewModel
    }

    private val _state = MutableStateFlow(ExamScreenUIState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<CreateExamScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private var getAllSubjectJob: Job? = null


    fun onUIEvent(event: ExamScreenUIEvent) {
        when (event) {
            is ExamScreenUIEvent.OnExamNameChanged -> onExamNameChanged(event.name)
            is ExamScreenUIEvent.OnExamSubjectPickerClick -> launch { onExamSubjectPickerClick() }
            is ExamScreenUIEvent.OnExamCategoryPickerClick -> launch { onExamCategoryPickerClick() }
            is ExamScreenUIEvent.OnSaveExamButtonClick -> launch { onSaveExamButtonClick() }
            is ExamScreenUIEvent.OnCategoryPicked -> onCategoryPicked(event.category)
            is ExamScreenUIEvent.OnSaveExamConfirmClick -> launch { onSaveExamConfirmClick() }
            is ExamScreenUIEvent.OnSubjectPicked -> onSubjectPicked(event.subject)
            is ExamScreenUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)
            is ExamScreenUIEvent.OnNavigateBackRequest -> onNavigateBackRequest()
            is ExamScreenUIEvent.OnNavigateToSubjectScreenRequest -> onNavigateToSubjectScreenRequest()
            is ExamScreenUIEvent.OnCustomExamDayReminderButtonClicked -> onCustomExamDayReminderButtonClicked()
            is ExamScreenUIEvent.OnCustomExamDeadlineReminderButtonClicked -> onCustomExamDeadlineReminderButtonClicked()
            is ExamScreenUIEvent.OnExamDayReminderChanged -> onExamDayReminderChanged(event.reminder)
            is ExamScreenUIEvent.OnExamDeadlineReminderChanged -> onExamDeadlineReminderChanged(event.reminder)
            is ExamScreenUIEvent.OnExamNotesChanged -> onExamNotesChanged(event.notes)
            is ExamScreenUIEvent.OnExamScoreChanged -> onExamScoreChanged(event.score)
            is ExamScreenUIEvent.OnPickExamPeriodButtonClicked -> onPickExamPeriodButtonClicked()
        }
    }

    private fun onPickExamPeriodButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + CreateExamScreenPopUp.DuePeriodPicker)
        }
    }

    private fun onExamScoreChanged(score: Int?) {
        viewModelScope.launch {
            eventRepository.updateExamScore(examId, score).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + CreateExamScreenPopUp.Loading)
                        }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CreateExamScreenPopUp.Loading)
                                    .plus(CreateExamScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown error")))
                            )
                        }
                    }
                    is Result.Success -> {
                        _state.update { it.copy(popUps = it.popUps - CreateExamScreenPopUp.Loading) }
                    }
                }
            }
        }
    }

    private fun onExamNotesChanged(notes: String) {
        _state.update {
            it.copy(notes = notes)
        }
    }

    private fun onExamDeadlineReminderChanged(reminder: ReminderOption) {
        _state.update {
            it.copy(deadlineReminder = reminder)
        }
    }

    private fun onExamDayReminderChanged(reminder: ReminderOption) {
        _state.update {
            it.copy(dueReminder = reminder)
        }
    }

    private fun onCustomExamDeadlineReminderButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + CreateExamScreenPopUp.CustomDeadlineReminderPicker)
        }
    }

    private fun onCustomExamDayReminderButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + CreateExamScreenPopUp.CustomDueReminderPicker)
        }
    }

    private fun onNavigateToSubjectScreenRequest() {
        _navigationEvent.trySend(CreateExamScreenNavigationEvent.NavigateToCreateSubject)
    }

    private fun onNavigateBackRequest() {
        _navigationEvent.trySend(CreateExamScreenNavigationEvent.NavigateBack)
    }

    private fun onDismissPopUp(popUp: CreateExamScreenPopUp) {
        _state.update {
            it.copy(popUps = it.popUps - popUp)
        }
    }

    private fun onSubjectPicked(subject: Subject) {
        _state.update { it.copy(subject = subject) }
    }

    private fun onCategoryPicked(category: ExamCategory) {
        _state.update { it.copy(category = category) }
    }

    init {
        if (examId != "-1") {
            viewModelScope.launch {
                eventRepository.getExamById(examId).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.Loading) }
                        }
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    popUps = it.popUps
                                        .minus(CreateExamScreenPopUp.Loading)
                                        .plus(
                                            CreateExamScreenPopUp.Error(
                                                errorMessage = UIText.DynamicString(result.throwable.message ?: "Unknown error")
                                            )
                                        )
                                )
                            }
                        }
                        is Result.Success -> {
                            _state.update { it.copy(popUps = it.popUps - CreateExamScreenPopUp.Loading) }
                            result.data.collect { examWithSubject ->
                                if (examWithSubject == null) return@collect
                                _state.update {
                                    it.copy(
                                        isEditMode = true,
                                        examTitle = examWithSubject.exam.title,
                                        dueDate = examWithSubject.exam.dueDate,
                                        deadline = examWithSubject.exam.deadline,
                                        dueReminder = with(examWithSubject.exam) {
                                            dueReminder?.toReminderOption(dueDate, ReminderType.DUE)
                                        },
                                        deadlineReminder = with(examWithSubject.exam) {
                                            deadlineReminder?.toReminderOption(deadline, ReminderType.DEADLINE)
                                        },
                                        subject = examWithSubject.subject,
                                        category = examWithSubject.exam.category,
                                        score = examWithSubject.exam.score,
                                        notes = examWithSubject.exam.notes
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
                        _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.Loading) }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CreateExamScreenPopUp.Loading)
                                    .plus(
                                        CreateExamScreenPopUp.Error(
                                            errorMessage = UIText.DynamicString(result.throwable.message ?: "Unknown error")
                                        )
                                    )
                            )
                        }
                    }
                    is Result.Success -> {
                        _state.update { it.copy(popUps = it.popUps - CreateExamScreenPopUp.Loading) }
                        result.data.collect { subjects ->
                            _state.update { it.copy(subjects = subjects) }
                        }
                    }
                }
            }
        }
    }

    private suspend fun onSaveExamButtonClick() {
        _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.SaveConfirmationDialog) }
    }

    private suspend fun onSaveExamConfirmClick() {
        try {
            val exam = Exam(
                id = if (examId != "-1") examId else UUID.randomUUID().toString(),
                title = _state.value.examTitle,
                dueDate = _state.value.dueDate,
                dueReminder = _state.value.dueReminder?.toLocalDateTime(_state.value.dueDate),
                deadline = _state.value.deadline,
                deadlineReminder = _state.value.deadlineReminder?.toLocalDateTime(_state.value.deadline),
                subjectId = _state.value.subject?.id ?: throw MissingRequiredFieldException.Subject(),
                category = _state.value.category,
                notes = _state.value.notes,
                score = _state.value.score,
            )
            viewModelScope.launch {
                if (examId == "-1") {
                    saveExam(exam)
                } else {
                    updateExam(exam)
                }
            }
        } catch (e: MissingRequiredFieldException) {
            val errorMessage = when (e) {
                is MissingRequiredFieldException.Subject -> UIText.StringResource(R.string.subject_cannot_be_empty)
                is MissingRequiredFieldException.Title -> UIText.StringResource(R.string.exam_name_cannot_be_empty)
            }
            _state.update {
                it.copy(popUps = it.popUps + CreateExamScreenPopUp.Error(errorMessage))
            }
        }
    }

    private suspend fun saveExam(exam: Exam) {
        eventRepository.saveExam(exam).collect { result ->
            when (result) {
                is Result.Loading -> {
                    _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.Loading) }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(CreateExamScreenPopUp.Loading)
                                .plus(
                                    CreateExamScreenPopUp.Error(
                                        errorMessage = UIText.DynamicString(result.throwable.message ?: "Unknown error")
                                    )
                                )
                        )
                    }
                }
                is Result.Success -> {
                    exam.dueReminder?.let { dueReminder ->
                        scheduleReminder(
                            context = application.applicationContext,
                            localDateTime = dueReminder,
                            title = exam.title,
                            reminderId = Pair(exam, dueReminder).hashCode()
                        )
                    }
                    exam.deadlineReminder?.let { deadlineReminder ->
                        scheduleReminder(
                            context = application.applicationContext,
                            localDateTime = deadlineReminder,
                            title = exam.title,
                            reminderId = Pair(exam, deadlineReminder).hashCode()
                        )
                    }
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(CreateExamScreenPopUp.Loading)
                                .plus(CreateExamScreenPopUp.ExamSavedDialog)
                        )
                    }
                }
            }
        }
    }

    private suspend fun updateExam(exam: Exam) {
        eventRepository.updateExam(exam).collect { result ->
            when (result) {
                is Result.Loading -> {
                    _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.Loading) }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(CreateExamScreenPopUp.Loading)
                                .plus(
                                    CreateExamScreenPopUp.Error(
                                        errorMessage = UIText.DynamicString(result.throwable.message ?: "Unknown error")
                                    )
                                )
                        )
                    }
                }
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(CreateExamScreenPopUp.Loading)
                                .plus(CreateExamScreenPopUp.ExamSavedDialog)
                        )
                    }
                }
            }
        }
    }

    private fun onExamNameChanged(name: String) {
        _state.value = _state.value.copy(examTitle = name)
    }

    private fun onExamSubjectPickerClick() {
        _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.SubjectPicker) }
    }

    private fun onExamCategoryPickerClick() {
        _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.CustomExamCategoryPicker) }
    }

    override fun onCleared() {
        getAllSubjectJob?.cancel()
        getAllSubjectJob = null
        super.onCleared()
    }

}