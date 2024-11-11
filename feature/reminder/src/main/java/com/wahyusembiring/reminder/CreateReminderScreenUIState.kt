package com.wahyusembiring.reminder

import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.ui.theme.primaryLight
import com.wahyusembiring.ui.util.UIText
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

data class CreateReminderScreenUIState(
    val isEditMode: Boolean = false,
    val title: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val color: Color = primaryLight,
    val isCompleted: Boolean = false,
    val attachments: List<Attachment> = emptyList(),
    val description: String = "",
    val popUps: List<CreateReminderScreenPopUp> = emptyList()
)

sealed class CreateReminderScreenPopUp {
    data object DatePicker : CreateReminderScreenPopUp()
    data object TimePicker : CreateReminderScreenPopUp()
    data object ColorPicker : CreateReminderScreenPopUp()
    data object AttachmentPicker : CreateReminderScreenPopUp()
    data object SaveConfirmationDialog : CreateReminderScreenPopUp()
    data object Loading : CreateReminderScreenPopUp()
    data object ReminderSavedDialog : CreateReminderScreenPopUp()
    data class Error(val errorMessage: UIText) : CreateReminderScreenPopUp()
}

sealed class CreateReminderScreenUIEvent {
    data class OnTitleChanged(val title: String) : CreateReminderScreenUIEvent()
    data class OnReminderDescriptionChanged(val title: String) : CreateReminderScreenUIEvent()
    data object OnDatePickerButtonClick : CreateReminderScreenUIEvent()
    data class OnDatePicked(val date: LocalDate) : CreateReminderScreenUIEvent()
    data object OnTimePickerButtonClick : CreateReminderScreenUIEvent()
    data class OnTimePicked(val time: LocalTime) : CreateReminderScreenUIEvent()
    data object OnColorPickerButtonClick : CreateReminderScreenUIEvent()
    data class OnColorPicked(val color: Color) : CreateReminderScreenUIEvent()
    data object OnAttachmentPickerButtonClick : CreateReminderScreenUIEvent()
    data class OnAttachmentPicked(val attachments: List<Attachment>) : CreateReminderScreenUIEvent()
    data object OnSaveButtonClicked : CreateReminderScreenUIEvent()
    data object OnSaveReminderConfirmClick : CreateReminderScreenUIEvent()
    data class OnPopDismiss(val popUp: CreateReminderScreenPopUp) : CreateReminderScreenUIEvent()
    data object OnReminderSavedOkButtonClick : CreateReminderScreenUIEvent()
    data object OnNavigateUpButtonClick : CreateReminderScreenUIEvent()
}

sealed class CreateReminderScreenNavigationEvent {
    data object NavigateUp : CreateReminderScreenNavigationEvent()
}