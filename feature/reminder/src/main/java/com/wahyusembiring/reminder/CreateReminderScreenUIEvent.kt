package com.wahyusembiring.reminder

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.Time
import java.util.Date

sealed class CreateReminderScreenUIEvent {

    data class OnTitleChanged(val title: String) : CreateReminderScreenUIEvent()
    data object OnDatePickerButtonClick : CreateReminderScreenUIEvent()
    data class OnDatePicked(val date: Date) : CreateReminderScreenUIEvent()
    data object OnDatePickerDismiss : CreateReminderScreenUIEvent()
    data object OnTimePickerButtonClick : CreateReminderScreenUIEvent()
    data class OnTimePicked(val time: Time) : CreateReminderScreenUIEvent()
    data object OnTimePickerDismiss : CreateReminderScreenUIEvent()
    data object OnColorPickerButtonClick : CreateReminderScreenUIEvent()
    data class OnColorPicked(val color: Color) : CreateReminderScreenUIEvent()
    data object OnColorPickerDismiss : CreateReminderScreenUIEvent()
    data object OnAttachmentPickerButtonClick : CreateReminderScreenUIEvent()
    data class OnAttachmentPicked(val attachments: List<Attachment>) : CreateReminderScreenUIEvent()
    data object OnAttachmentPickerDismiss : CreateReminderScreenUIEvent()
    data object OnSaveButtonClicked : CreateReminderScreenUIEvent()
    data object OnSaveReminderConfirmClick : CreateReminderScreenUIEvent()
    data object OnSaveConfirmationDialogDismiss : CreateReminderScreenUIEvent()
    data object OnReminderSavedDialogDismiss : CreateReminderScreenUIEvent()
    data object OnErrorDialogDismiss : CreateReminderScreenUIEvent()

}