package com.wahyusembiring.reminder

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.ui.theme.primaryLight
import com.wahyusembiring.ui.util.UIText
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date

data class CreateReminderScreenUIState(
    val isEditMode: Boolean = false,
    val title: String = "",
    val reminderDates: List<LocalDateTime> = emptyList(),
    val notes: String = "",
    val popUps: List<CreateReminderScreenPopUp> = emptyList()
)

sealed class CreateReminderScreenPopUp {
    data object DateTimePicker : CreateReminderScreenPopUp()
    data object SaveConfirmationDialog : CreateReminderScreenPopUp()
    data object Loading : CreateReminderScreenPopUp()
    data object ReminderSavedDialog : CreateReminderScreenPopUp()
    data class Error(val errorMessage: UIText) : CreateReminderScreenPopUp()
}

sealed class CreateReminderScreenUIEvent {
    data class OnAddReminderDate(val date: LocalDateTime) : CreateReminderScreenUIEvent()
    data class OnNotesChanged(val notes: String): CreateReminderScreenUIEvent()
    data class OnDeleteReminderDateButtonClick(val date: LocalDateTime) : CreateReminderScreenUIEvent()
    data class OnTitleChanged(val title: String) : CreateReminderScreenUIEvent()
    data object OnAddReminderDateButtonClick : CreateReminderScreenUIEvent()
    data object OnSaveButtonClicked : CreateReminderScreenUIEvent()
    data object OnSaveReminderConfirmClick : CreateReminderScreenUIEvent()
    data class OnPopDismiss(val popUp: CreateReminderScreenPopUp) : CreateReminderScreenUIEvent()
    data object OnReminderSavedOkButtonClick : CreateReminderScreenUIEvent()
    data object OnNavigateUpButtonClick : CreateReminderScreenUIEvent()
}

sealed class CreateReminderScreenNavigationEvent {
    data object NavigateUp : CreateReminderScreenNavigationEvent()
}