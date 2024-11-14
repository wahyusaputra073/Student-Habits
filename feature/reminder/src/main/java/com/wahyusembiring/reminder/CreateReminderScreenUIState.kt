package com.wahyusembiring.reminder

import androidx.compose.ui.graphics.Color
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.DeadlineTime
import com.wahyusembiring.data.model.OfficeHour
import com.wahyusembiring.data.model.SpanTime
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.ui.theme.primaryLight
import com.wahyusembiring.ui.util.UIText
import java.util.Date

data class CreateReminderScreenUIState(
    val isEditMode: Boolean = false,
    val title: String = "",
    val date: Date? = null,
    val time: Time? = null,
    val spanTime: SpanTime? = null ,
    val color: Color = primaryLight,
    val isCompleted: Boolean = false,
    val attachments: List<Attachment> = emptyList(),
    val description: String = "",

    //popup
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showDuraPicker: Boolean = false,
    val showColorPicker: Boolean = false,
    val showAttachmentPicker: Boolean = false,
    val showSaveConfirmationDialog: Boolean = false,
    val showSavingLoading: Boolean = false,
    val showReminderSavedDialog: Boolean = false,
    val errorMessage: UIText? = null

)