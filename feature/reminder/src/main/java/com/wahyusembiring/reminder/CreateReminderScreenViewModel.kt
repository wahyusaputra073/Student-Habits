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
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.repository.EventRepository
import com.wahyusembiring.ui.util.UIText
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
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
import java.time.ZoneOffset
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel(assistedFactory = CreateReminderScreenViewModel.Factory::class)
class CreateReminderScreenViewModel @AssistedInject constructor(
    @Assisted val reminderId: String = "-1",
    private val eventRepository: EventRepository,
    private val application: Application
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(reminderId: String = "-1"): CreateReminderScreenViewModel
    }

    private val _state = MutableStateFlow(CreateReminderScreenUIState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<CreateReminderScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()


    init {
        if (reminderId != "-1") {
            viewModelScope.launch {
                eventRepository.getReminderById(reminderId).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.Loading) }
                        }
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    popUps = it.popUps
                                        .minus(CreateReminderScreenPopUp.Loading)
                                        .plus(CreateReminderScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown error")))
                                )
                            }
                        }
                        is Result.Success -> {
                            _state.update { it.copy(popUps = it.popUps - CreateReminderScreenPopUp.Loading) }
                            result.data.collect { reminderDto ->
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
            }
        }
    }

    fun onUIEvent(event: CreateReminderScreenUIEvent) {
        when (event) {
            is CreateReminderScreenUIEvent.OnTitleChanged -> onTitleChanged(event.title)
            is CreateReminderScreenUIEvent.OnReminderDescriptionChanged -> onReminderDescriptionChanged(event.title)
            is CreateReminderScreenUIEvent.OnDatePickerButtonClick -> launch { onDatePickerButtonClick() }
            is CreateReminderScreenUIEvent.OnTimePickerButtonClick -> launch { onTimePickerButtonClick() }
            is CreateReminderScreenUIEvent.OnColorPickerButtonClick -> launch { onColorPickerButtonClick() }
            is CreateReminderScreenUIEvent.OnAttachmentPickerButtonClick -> launch { onAttachmentPickerButtonClick() }
            is CreateReminderScreenUIEvent.OnSaveButtonClicked -> launch { onSaveButtonClicked() }
            is CreateReminderScreenUIEvent.OnAttachmentPicked -> onAttachmentPicked(event.attachments)
            is CreateReminderScreenUIEvent.OnColorPicked -> onColorPicked(event.color)
            is CreateReminderScreenUIEvent.OnDatePicked -> onDatePicked(event.date)
            is CreateReminderScreenUIEvent.OnSaveReminderConfirmClick -> launch { onSaveReminderConfirmClick() }
            is CreateReminderScreenUIEvent.OnTimePicked -> onTimePicked(event.time)
            is CreateReminderScreenUIEvent.OnNavigateUpButtonClick -> onNavigateUpButtonClick()
            is CreateReminderScreenUIEvent.OnPopDismiss -> onPopDismiss(event.popUp)
            is CreateReminderScreenUIEvent.OnReminderSavedOkButtonClick -> onReminderSavedOkButtonClick()
        }
    }

    private fun onReminderSavedOkButtonClick() {
        _navigationEvent.trySend(CreateReminderScreenNavigationEvent.NavigateUp)
    }

    private fun onPopDismiss(popUp: CreateReminderScreenPopUp) {
        _state.update { it.copy(popUps = it.popUps - popUp) }
    }

    private fun onNavigateUpButtonClick() {
        _navigationEvent.trySend(CreateReminderScreenNavigationEvent.NavigateUp)
    }

    private fun onTimePicked(time: LocalTime) {
        _state.update { it.copy(time = time) }
    }

    private fun onDatePicked(date: LocalDate) {
        _state.update { it.copy(date = date) }
    }

    private fun onColorPicked(color: Color) {
        _state.update { it.copy(color = color) }
    }

    private fun onAttachmentPicked(attachments: List<Attachment>) {
        _state.update { it.copy(attachments = attachments) }
    }


    private fun onSaveButtonClicked() {
        _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.SaveConfirmationDialog) }
    }

    private fun onSaveReminderConfirmClick() {
        try {
            val reminder = Reminder(
                id = if (reminderId != "-1") reminderId else UUID.randomUUID().toString(),
                title = _state.value.title.ifBlank { throw MissingRequiredFieldException.Title() },
                date = _state.value.date ?: throw MissingRequiredFieldException.Date(),
                time = _state.value.time ?: throw MissingRequiredFieldException.Date(),
                color = _state.value.color,
                attachments = _state.value.attachments,
                description = _state.value.description,
                completed = _state.value.isCompleted
            )
            viewModelScope.launch {
                if (reminderId == "-1") {
                    saveReminder(reminder)
                } else {
                    updateReminder(reminder)
                }
            }
        } catch (e: MissingRequiredFieldException) {
            val errorMessage = when (e) {
                is MissingRequiredFieldException.Title -> UIText.StringResource(R.string.title_is_required)
                is MissingRequiredFieldException.Date -> UIText.StringResource(R.string.date_is_required)
                is MissingRequiredFieldException.Time -> UIText.StringResource(R.string.time_is_required)
            }
            _state.update {
                it.copy(popUps = it.popUps + CreateReminderScreenPopUp.Error(errorMessage))
            }
        }
    }

    private suspend fun saveReminder(reminder: Reminder) {
        eventRepository.saveReminder(reminder).collect { result ->
            when (result) {
                is Result.Loading -> {
                    _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.Loading) }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(CreateReminderScreenPopUp.Loading)
                                .plus(CreateReminderScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown error")))
                        )
                    }
                }
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(CreateReminderScreenPopUp.Loading)
                                .plus(CreateReminderScreenPopUp.ReminderSavedDialog)
                        )
                    }
                }
            }
        }
        scheduleReminder(
            context = application.applicationContext,
            localDateTime = LocalDateTime.of(reminder.date, reminder.time),
            title = reminder.title,
            reminderId = reminder.id.hashCode()
        )
    }

    private suspend fun updateReminder(reminder: Reminder) {
        eventRepository.updateReminder(reminder).collect { result ->
            when (result) {
                is Result.Loading -> {
                    _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.Loading) }
                }
                is Result.Error -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(CreateReminderScreenPopUp.Loading)
                                .plus(CreateReminderScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown error")))
                        )
                    }
                }
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            popUps = it.popUps
                                .minus(CreateReminderScreenPopUp.Loading)
                                .plus(CreateReminderScreenPopUp.ReminderSavedDialog)
                        )
                    }
                }
            }
        }
        scheduleReminder(
            context = application.applicationContext,
            localDateTime = LocalDateTime.of(reminder.date, reminder.time),
            title = reminder.title,
            reminderId = reminder.id.hashCode()
        )
    }


    private fun onTitleChanged(title: String) {
        _state.update {
            it.copy(title = title)
        }
    }

    private fun onReminderDescriptionChanged(description: String) {
        _state.update {
            it.copy(description = description)
        }
    }

    private fun onDatePickerButtonClick() {
        _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.DatePicker) }
    }

    private fun onTimePickerButtonClick() {
        _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.TimePicker) }
    }

    private fun onColorPickerButtonClick() {
        _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.ColorPicker) }
    }

    private fun onAttachmentPickerButtonClick() {
        _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.AttachmentPicker) }
    }

}