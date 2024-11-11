package com.wahyusembiring.exam

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.launch
import com.wahyusembiring.common.util.scheduleReminder
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.DeadlineTime
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

@HiltViewModel(assistedFactory = ExamScreenViewModel.Factory::class)
class ExamScreenViewModel @AssistedInject constructor(
    @Assisted val examId: Int = -1,
    private val eventRepository: EventRepository,
    private val subjectRepository: SubjectRepository,
    private val application: Application
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(examId: Int = -1): ExamScreenViewModel
    }

    private val _state = MutableStateFlow(ExamScreenUIState())
    val state = _state.asStateFlow()

    private var getAllSubjectJob: Job? = null


    fun onUIEvent(event: ExamScreenUIEvent) {
        when (event) {
            is ExamScreenUIEvent.OnExamNameChanged -> onExamNameChanged(event.name)
            is ExamScreenUIEvent.OnExamDescriptionChanged -> onExamDescriptionChanged(event.name)
            is ExamScreenUIEvent.OnExamDatePickerClick -> launch { onExamDatePickerClick() }
            is ExamScreenUIEvent.OnExamTimePickerClick -> launch { onExamTimePickerClick() }
            is ExamScreenUIEvent.OnExamDeadlineTimePickerClick -> launch { onExamDeadlineTimePickerClick() }
            is ExamScreenUIEvent.OnExamSubjectPickerClick -> launch { onExamSubjectPickerClick() }
            is ExamScreenUIEvent.OnExamAttachmentPickerClick -> launch { onExamAttachmentPickerClick() }
            is ExamScreenUIEvent.OnExamCategoryPickerClick -> launch { onExamCategoryPickerClick() }
            is ExamScreenUIEvent.OnSaveExamButtonClick -> launch { onSaveExamButtonClick() }
            is ExamScreenUIEvent.OnAttachmentPicked -> onAttachmentPicked(event.attachments)
            is ExamScreenUIEvent.OnAttachmentPickedDismiss -> onAttachmentPickedDismiss()
            is ExamScreenUIEvent.OnCategoryPicked -> onCategoryPicked(event.category)
            is ExamScreenUIEvent.OnCategoryPickedDismiss -> onCategoryPickedDismiss()
            is ExamScreenUIEvent.OnDatePicked -> onDatePicked(event.date)
            is ExamScreenUIEvent.OnDatePickedDismiss -> onDatePickedDismiss()
            is ExamScreenUIEvent.OnErrorDialogDismiss -> onErrorDialogDismiss()
            is ExamScreenUIEvent.OnExamSavedDialogDismiss -> onExamSavedDialogDismiss()
            is ExamScreenUIEvent.OnSaveConfirmationDialogDismiss -> onSaveConfirmationDialogDismiss()
            is ExamScreenUIEvent.OnSaveExamConfirmClick -> launch { onSaveExamConfirmClick() }
            is ExamScreenUIEvent.OnSubjectPicked -> onSubjectPicked(event.subject)
            is ExamScreenUIEvent.OnSubjectPickedDismiss -> onSubjectPickedDismiss()
            is ExamScreenUIEvent.OnTimePicked -> onTimePicked(event.time)
            is ExamScreenUIEvent.OnDeadlineTimePicked -> onDeadlineTimePicked(event.times)
            is ExamScreenUIEvent.OnTimePickedDismiss -> onTimePickedDismiss()
            is ExamScreenUIEvent.OnDeadlineTimePickedDismiss -> onDeadlineTimePickedDismiss()
        }
    }

    private fun onTimePickedDismiss() {
        _state.update { it.copy(showTimePicker = false) }
    }

    private fun onDeadlineTimePickedDismiss() {
        _state.update { it.copy(showDeadlineTimePicker = false) }
    }

    private fun onDeadlineTimePicked(times: DeadlineTime) {
        _state.update { it.copy(times = times) }
    }

    private fun onTimePicked(time: Time) {
        _state.update { it.copy(time = time) }
    }



    private fun onSubjectPickedDismiss() {
        _state.update { it.copy(showSubjectPicker = false) }
    }

    private fun onSubjectPicked(subject: Subject) {
        _state.update { it.copy(subject = subject) }
    }

    private fun onSaveConfirmationDialogDismiss() {
        _state.update { it.copy(showSaveConfirmationDialog = false) }
    }

    private fun onExamSavedDialogDismiss() {
        _state.update { it.copy(showExamSavedDialog = false) }
    }

    private fun onErrorDialogDismiss() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun onDatePickedDismiss() {
        _state.update { it.copy(showDatePicker = false) }
    }

    private fun onDatePicked(date: Date) {
        _state.update { it.copy(date = date) }
    }

    private fun onCategoryPickedDismiss() {
        _state.update { it.copy(showCategoryPicker = false) }
    }

    private fun onCategoryPicked(category: ExamCategory) {
        _state.update { it.copy(category = category) }
    }

    private fun onAttachmentPickedDismiss() {
        _state.update { it.copy(showAttachmentPicker = false) }
    }

    private fun onAttachmentPicked(attachments: List<Attachment>) {
        _state.update { it.copy(attachments = attachments) }
    }

    init {
        if (examId != -1) {
            viewModelScope.launch {
                eventRepository.getExamById(examId).collect { examWithSubject ->
                    if (examWithSubject == null) return@collect
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            name = examWithSubject.exam.title,
                            date = examWithSubject.exam.date,
                            time = examWithSubject.exam.reminder,
                            times = examWithSubject.exam.deadline,
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
        getAllSubjectJob = viewModelScope.launch {
            subjectRepository.getAllSubject().collect { subjects ->
                _state.update {
                    it.copy(subjects = subjects)
                }
            }
        }
    }

    private suspend fun onSaveExamButtonClick() {
        _state.update { it.copy(showSaveConfirmationDialog = true) }
    }

    private suspend fun onSaveExamConfirmClick() {
        _state.update { it.copy(showSavingLoading = true) }
        try {
            val exam = Exam(
                id = if (examId != -1) examId else 0,
                title = _state.value.name,
                date = _state.value.date ?: throw MissingRequiredFieldException.Date(),
                reminder = _state.value.time ?: throw MissingRequiredFieldException.Time(),
                deadline = _state.value.times ?: throw MissingRequiredFieldException.Times(),
                subjectId = _state.value.subject?.id?: throw MissingRequiredFieldException.Subject(),
                category = _state.value.category,
                description = _state.value.description,
                attachments = _state.value.attachments,
                score = _state.value.score
            )
            val newExamId = if (examId == -1) {
                eventRepository.saveExam(exam)
            } else {
                eventRepository.updateExam(exam)
                examId
            }
            scheduleReminder(
                context = application.applicationContext,
                localDateTime = LocalDateTime.of(
                    LocalDate.ofInstant(exam.date.toInstant(), ZoneId.systemDefault()),
                    LocalTime.of(exam.reminder!!.hour, exam.reminder!!.minute)
                ),
                title = exam.title,
                reminderId = newExamId.toInt()
            )
            _state.update {
                it.copy(
                    showSavingLoading = false,
                    showExamSavedDialog = true
                )
            }
        } catch (e: MissingRequiredFieldException) {
            _state.update { it.copy(showSavingLoading = false) }
            val errorMessage = when (e) {
                is MissingRequiredFieldException.Date -> UIText.StringResource(R.string.date_cannot_be_empty)
                is MissingRequiredFieldException.Subject -> UIText.StringResource(R.string.subject_cannot_be_empty)
                is MissingRequiredFieldException.Time -> UIText.StringResource(R.string.time_cannot_be_empty)
                is MissingRequiredFieldException.Times -> UIText.StringResource(R.string.deadline_time_cannot_be_empty)
                is MissingRequiredFieldException.Title -> UIText.StringResource(R.string.exam_name_cannot_be_empty)
            }
            _state.update { it.copy(errorMessage = errorMessage) }
        }
    }

    private fun onExamNameChanged(name: String) {
        _state.value = _state.value.copy(name = name)
    }

    private fun onExamDescriptionChanged(description: String) {
        _state.value = _state.value.copy(description = description)
    }

    private fun onExamDatePickerClick() {
        _state.update { it.copy(showDatePicker = true) }
    }

    private fun onExamTimePickerClick() {
        _state.update { it.copy(showTimePicker = true) }
    }

    private fun onExamDeadlineTimePickerClick() {
        _state.update { it.copy(showDeadlineTimePicker = true) }
    }

    private fun onExamSubjectPickerClick() {
        _state.update { it.copy(showSubjectPicker = true) }
    }

    private fun onExamCategoryPickerClick() {
        _state.update { it.copy(showCategoryPicker = true) }
    }

    private fun onExamAttachmentPickerClick() {
        _state.update { it.copy(showAttachmentPicker = true) }
    }

    override fun onCleared() {
        getAllSubjectJob?.cancel()
        getAllSubjectJob = null
        super.onCleared()
    }

}