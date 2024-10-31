package com.wahyusembiring.reminder

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.launch
import com.wahyusembiring.common.util.scheduleReminder
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.repository.EventRepository
import com.wahyusembiring.ui.util.UIText
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Date
import javax.inject.Inject

@HiltViewModel(assistedFactory = CreateReminderScreenViewModel.Factory::class)
class CreateReminderScreenViewModel @AssistedInject constructor(
    @Assisted val reminderId: Int = -1,
    private val eventRepository: EventRepository,
    private val application: Application
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(reminderId: Int = -1): CreateReminderScreenViewModel
    }

    private val _state = MutableStateFlow(CreateReminderScreenUIState())
    val state = _state.asStateFlow()


    init {
        if (reminderId != -1) {
            viewModelScope.launch {
                eventRepository.getReminderById(reminderId).collect { reminderDto ->
                    if (reminderDto == null) return@collect
                    _state.update {
                        it.copy(
                            isEditMode = true,
                            title = reminderDto.title,
                            date = reminderDto.date,
                            time = reminderDto.time,
                            color = reminderDto.color,
                            attachments = reminderDto.attachments,
                            description = reminderDto.description,
                            isCompleted = reminderDto.completed
                        )
                    }
                }
            }
        }
    }

    fun onUIEvent(event: CreateReminderScreenUIEvent) {
        when (event) {
            is CreateReminderScreenUIEvent.OnTitleChanged -> onTitleChanged(event.title)
            is CreateReminderScreenUIEvent.OnDatePickerButtonClick -> launch { onDatePickerButtonClick() }
            is CreateReminderScreenUIEvent.OnTimePickerButtonClick -> launch { onTimePickerButtonClick() }
            is CreateReminderScreenUIEvent.OnColorPickerButtonClick -> launch { onColorPickerButtonClick() }
            is CreateReminderScreenUIEvent.OnAttachmentPickerButtonClick -> launch { onAttachmentPickerButtonClick() }
            is CreateReminderScreenUIEvent.OnSaveButtonClicked -> launch { onSaveButtonClicked() }
            is CreateReminderScreenUIEvent.OnAttachmentPicked -> onAttachmentPicked(event.attachments)
            is CreateReminderScreenUIEvent.OnAttachmentPickerDismiss -> onAttachmentPickerDismiss()
            is CreateReminderScreenUIEvent.OnColorPicked -> onColorPicked(event.color)
            is CreateReminderScreenUIEvent.OnColorPickerDismiss -> onColorPickerDismiss()
            is CreateReminderScreenUIEvent.OnDatePicked -> onDatePicked(event.date)
            is CreateReminderScreenUIEvent.OnDatePickerDismiss -> onDatePickerDismiss()
            is CreateReminderScreenUIEvent.OnErrorDialogDismiss -> onErrorDialogDismiss()
            is CreateReminderScreenUIEvent.OnReminderSavedDialogDismiss -> onReminderSavedDialogDismiss()
            is CreateReminderScreenUIEvent.OnSaveConfirmationDialogDismiss -> onSaveConfirmationDialogDismiss()
            is CreateReminderScreenUIEvent.OnSaveReminderConfirmClick -> launch { onSaveReminderConfirmClick() }
            is CreateReminderScreenUIEvent.OnTimePicked -> onTimePicked(event.time)
            is CreateReminderScreenUIEvent.OnTimePickerDismiss -> onTimePickerDismiss()
        }
    }

    private fun onTimePickerDismiss() {
        _state.update { it.copy(showTimePicker = false) }
    }

    private fun onTimePicked(time: Time) {
        _state.update { it.copy(time = time) }
    }

    private fun onSaveConfirmationDialogDismiss() {
        _state.update { it.copy(showSaveConfirmationDialog = false) }
    }

    private fun onReminderSavedDialogDismiss() {
        _state.update { it.copy(showReminderSavedDialog = false) }
    }

    private fun onErrorDialogDismiss() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun onDatePickerDismiss() {
        _state.update { it.copy(showDatePicker = false) }
    }

    private fun onDatePicked(date: Date) {
        _state.update { it.copy(date = date) }
    }

    private fun onColorPickerDismiss() {
        _state.update { it.copy(showColorPicker = false) }
    }

    private fun onColorPicked(color: Color) {
        _state.update { it.copy(color = color) }
    }

    private fun onAttachmentPickerDismiss() {
        _state.update { it.copy(showAttachmentPicker = false) }
    }

    private fun onAttachmentPicked(attachments: List<Attachment>) {
        _state.update { it.copy(attachments = attachments) }
    }


    private fun onSaveButtonClicked() {
        _state.update { it.copy(showSaveConfirmationDialog = true) }
    }

    private suspend fun onSaveReminderConfirmClick() {
        _state.update { it.copy(showSavingLoading = true) }
        try {
            saveReminder()
            _state.update {
                it.copy(
                    showSavingLoading = false,
                    showReminderSavedDialog = true
                )
            }
        } catch (e: MissingRequiredFieldException) {
            _state.update { it.copy(showSavingLoading = false) }
            val errorMessage = when (e) {
                is MissingRequiredFieldException.Title -> UIText.StringResource(R.string.title_is_required)
                is MissingRequiredFieldException.Date -> UIText.StringResource(R.string.date_is_required)
                is MissingRequiredFieldException.Time -> UIText.StringResource(R.string.time_is_required)
            }
            _state.update { it.copy(errorMessage = errorMessage) }
        }
    }

    private suspend fun saveReminder() {
        val reminder = Reminder(
            id = if (reminderId != -1) reminderId else 0,
            title = _state.value.title.ifBlank { throw MissingRequiredFieldException.Title() },
            date = _state.value.date ?: throw MissingRequiredFieldException.Date(),
            time = _state.value.time ?: throw MissingRequiredFieldException.Date(),
            color = _state.value.color,
            attachments = _state.value.attachments,
            description = _state.value.description,
            completed = _state.value.isCompleted
        )
        val savedReminderId = if (reminderId == -1) {
            eventRepository.saveReminder(reminder)
        } else {
            eventRepository.updateReminder(reminder)
            reminderId
        }
        scheduleReminder(
            context = application.applicationContext,
            localDateTime = LocalDateTime.of(
                LocalDate.ofInstant(reminder.date.toInstant(), ZoneId.systemDefault()),
                LocalTime.of(reminder.time.hour, reminder.time.minute)
            ),
            title = reminder.title,
            reminderId = savedReminderId.toInt()
        )
    }


    private fun onTitleChanged(title: String) {
        _state.update {
            it.copy(title = title)
        }
    }

    private fun onDatePickerButtonClick() {
        _state.update { it.copy(showDatePicker = true) }
    }

    private fun onTimePickerButtonClick() {
        _state.update { it.copy(showTimePicker = true) }
    }

    private fun onColorPickerButtonClick() {
        _state.update { it.copy(showColorPicker = true) }
    }

    private fun onAttachmentPickerButtonClick() {
        _state.update { it.copy(showAttachmentPicker = true) }
    }

}