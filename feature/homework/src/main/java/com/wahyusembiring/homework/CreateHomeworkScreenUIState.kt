package com.wahyusembiring.homework

import android.net.Uri
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.DeadlineTime
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.ui.util.UIText
import java.util.Date

data class CreateHomeworkScreenUIState(
    val isEditMode: Boolean = false,
    val homeworkTitle: String = "",
    val date: Date? = null,
    val time: Time? = null,
    val times: DeadlineTime? = null,
    val subjects: List<Subject> = emptyList(),
    val subject: Subject? = null,
    val attachments: List<Attachment> = emptyList(),
    val isCompleted: Boolean = false,
    val description: String = "",

    // popup
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showDeadlineTimePicker: Boolean = false,
    val showSubjectPicker: Boolean = false,
    val showAttachmentPicker: Boolean = false,
    val showSaveConfirmationDialog: Boolean = false,
    val showHomeworkSavedDialog: Boolean = false,
    val showSavingLoading: Boolean = false,
    val errorMessage: UIText? = null,
)