package com.wahyusembiring.exam

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.launch
import com.wahyusembiring.common.util.scheduleReminder
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.repository.EventRepository
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
            is ExamScreenUIEvent.OnExamDescriptionChanged -> onExamDescriptionChanged(event.name)
            is ExamScreenUIEvent.OnExamDatePickerClick -> launch { onExamDatePickerClick() }
            is ExamScreenUIEvent.OnExamTimePickerClick -> launch { onExamTimePickerClick() }
            is ExamScreenUIEvent.OnExamSubjectPickerClick -> launch { onExamSubjectPickerClick() }
            is ExamScreenUIEvent.OnExamAttachmentPickerClick -> launch { onExamAttachmentPickerClick() }
            is ExamScreenUIEvent.OnExamCategoryPickerClick -> launch { onExamCategoryPickerClick() }
            is ExamScreenUIEvent.OnSaveExamButtonClick -> launch { onSaveExamButtonClick() }
            is ExamScreenUIEvent.OnAttachmentPicked -> onAttachmentPicked(event.attachments)
            is ExamScreenUIEvent.OnCategoryPicked -> onCategoryPicked(event.category)
            is ExamScreenUIEvent.OnDatePicked -> onDatePicked(event.date)
            is ExamScreenUIEvent.OnSaveExamConfirmClick -> launch { onSaveExamConfirmClick() }
            is ExamScreenUIEvent.OnSubjectPicked -> onSubjectPicked(event.subject)
            is ExamScreenUIEvent.OnTimePicked -> onTimePicked(event.time)
            is ExamScreenUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)
            is ExamScreenUIEvent.OnNavigateBackRequest -> onNavigateBackRequest()
            is ExamScreenUIEvent.OnNavigateToSubjectScreenRequest -> onNavigateToSubjectScreenRequest()
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

    private fun onTimePicked(time: LocalTime) {
        _state.update { it.copy(time = time) }
    }

    private fun onSubjectPicked(subject: Subject) {
        _state.update { it.copy(subject = subject) }
    }

    private fun onDatePicked(date: LocalDate) {
        _state.update { it.copy(date = date) }
    }

    private fun onCategoryPicked(category: ExamCategory) {
        _state.update { it.copy(category = category) }
    }

    private fun onAttachmentPicked(attachments: List<Attachment>) {
        _state.update { it.copy(attachments = attachments) }
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
                                        name = examWithSubject.exam.title,
                                        date = examWithSubject.exam.date,
                                        time = examWithSubject.exam.reminder,
                                        subject = examWithSubject.subject,
                                        category = examWithSubject.exam.category,
                                        score = examWithSubject.exam.score,
                                        attachments = examWithSubject.exam.attachments,
                                        description = examWithSubject.exam.description
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
                title = _state.value.name,
                date = _state.value.date ?: throw MissingRequiredFieldException.Date(),
                reminder = _state.value.time ?: throw MissingRequiredFieldException.Time(),
                subjectId = _state.value.subject?.id
                    ?: throw MissingRequiredFieldException.Subject(),
                category = _state.value.category,
                description = _state.value.description,
                attachments = _state.value.attachments,
                score = _state.value.score
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
                is MissingRequiredFieldException.Date -> UIText.StringResource(R.string.date_cannot_be_empty)
                is MissingRequiredFieldException.Subject -> UIText.StringResource(R.string.subject_cannot_be_empty)
                is MissingRequiredFieldException.Time -> UIText.StringResource(R.string.time_cannot_be_empty)
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
        scheduleReminder(
            context = application.applicationContext,
            localDateTime = LocalDateTime.of(
                exam.date,
                exam.reminder
            ),
            title = exam.title,
            reminderId = exam.id.hashCode()
        )
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
        scheduleReminder(
            context = application.applicationContext,
            localDateTime = LocalDateTime.of(
                exam.date,
                exam.reminder
            ),
            title = exam.title,
            reminderId = exam.id.hashCode()
        )
    }

    private fun onExamNameChanged(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    private fun onExamDescriptionChanged(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    private fun onExamDatePickerClick() {
        _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.DatePicker) }
    }

    private fun onExamTimePickerClick() {
        _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.TimePicker) }
    }

    private fun onExamSubjectPickerClick() {
        _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.SubjectPicker) }
    }

    private fun onExamCategoryPickerClick() {
        _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.ExamCategoryPicker) }
    }

    private fun onExamAttachmentPickerClick() {
        _state.update { it.copy(popUps = it.popUps + CreateExamScreenPopUp.AttachmentPicker) }
    }

    override fun onCleared() {
        getAllSubjectJob?.cancel()
        getAllSubjectJob = null
        super.onCleared()
    }

}