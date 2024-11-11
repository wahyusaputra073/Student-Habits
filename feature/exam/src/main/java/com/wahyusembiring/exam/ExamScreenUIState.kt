package com.wahyusembiring.exam

import android.net.Uri
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.DeadlineTime
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.ui.util.UIText
import java.util.Date

data class ExamScreenUIState(
    val isEditMode: Boolean = false,
    val name: String = "",
    val description: String = "",
    val date: Date? = null,
    val time: Time? = null,
    val times: DeadlineTime? = null,
    val subjects: List<Subject> = emptyList(),
    val subject: Subject? = null,
    val score: Int? = null,
    val category: ExamCategory = ExamCategory.WRITTEN,
    val attachments: List<Attachment> = emptyList(),

    // popups
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val showDeadlineTimePicker: Boolean = false,
    val showSubjectPicker: Boolean = false,
    val showAttachmentPicker: Boolean = false,
    val showSaveConfirmationDialog: Boolean = false,
    val showExamSavedDialog: Boolean = false,
    val showSavingLoading: Boolean = false,
    val showCategoryPicker: Boolean = false,
    val errorMessage: UIText? = null,
)