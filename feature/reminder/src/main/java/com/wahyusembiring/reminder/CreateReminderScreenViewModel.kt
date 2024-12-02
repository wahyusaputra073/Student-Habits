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
                                        reminderDates = reminderDto.reminderDates,
                                        notes = reminderDto.notes
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
            is CreateReminderScreenUIEvent.OnSaveButtonClicked -> launch { onSaveButtonClicked() }
            is CreateReminderScreenUIEvent.OnSaveReminderConfirmClick -> launch { onSaveReminderConfirmClick() }
            is CreateReminderScreenUIEvent.OnNavigateUpButtonClick -> onNavigateUpButtonClick()
            is CreateReminderScreenUIEvent.OnPopDismiss -> onPopDismiss(event.popUp)
            is CreateReminderScreenUIEvent.OnReminderSavedOkButtonClick -> onReminderSavedOkButtonClick()
            is CreateReminderScreenUIEvent.OnAddReminderDate -> onAddReminderDate(event.date)
            is CreateReminderScreenUIEvent.OnAddReminderDateButtonClick -> onAddReminderDateButtonClick()
            is CreateReminderScreenUIEvent.OnDeleteReminderDateButtonClick -> onDeleteReminderDateButtonClick(event.date)
            is CreateReminderScreenUIEvent.OnNotesChanged -> onNotesChanged(event.notes)
        }
    }

    private fun onNotesChanged(notes: String) {
        _state.update { it.copy(notes = notes) }
    }

    private fun onDeleteReminderDateButtonClick(date: LocalDateTime) {
        _state.update { it.copy(reminderDates = it.reminderDates - date) }
    }

    private fun onAddReminderDateButtonClick() {
        _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.DateTimePicker) }
    }

    private fun onAddReminderDate(dateTime: LocalDateTime) {
        _state.update { it.copy(reminderDates = it.reminderDates + dateTime) }
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

    private fun onSaveButtonClicked() {
        _state.update { it.copy(popUps = it.popUps + CreateReminderScreenPopUp.SaveConfirmationDialog) }
    }

    private fun onSaveReminderConfirmClick() {
        try {
            val reminder = Reminder(
                id = if (reminderId != "-1") reminderId else UUID.randomUUID().toString(),
                title = _state.value.title.ifBlank { throw MissingRequiredFieldException.Title() },
                reminderDates = _state.value.reminderDates.ifEmpty { throw MissingRequiredFieldException.DateTime() },
                notes = _state.value.notes
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
                is MissingRequiredFieldException.DateTime -> UIText.StringResource(R.string.at_least_one_date_is_required)
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
                    reminder.reminderDates.forEach {
                        scheduleReminder(
                            context = application.applicationContext,
                            localDateTime = it,
                            title = reminder.title,
                            reminderId = Pair(reminder, it).hashCode()
                        )
                    }
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
                    reminder.reminderDates.forEach {
                        scheduleReminder(
                            context = application.applicationContext,
                            localDateTime = it,
                            title = reminder.title,
                            reminderId = Pair(reminder, it).hashCode()
                        )
                    }
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
    }


    private fun onTitleChanged(title: String) {
        _state.update {
            it.copy(title = title)
        }
    }
}