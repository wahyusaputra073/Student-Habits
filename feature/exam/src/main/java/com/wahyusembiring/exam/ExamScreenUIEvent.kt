package com.wahyusembiring.exam

import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.data.model.entity.Subject
import java.util.Date

sealed class ExamScreenUIEvent {
    data class OnExamNameChanged(val name: String) : ExamScreenUIEvent()
    data object OnExamDatePickerClick : ExamScreenUIEvent()
    data class OnDatePicked(val date: Date) : ExamScreenUIEvent()
    data object OnDatePickedDismiss : ExamScreenUIEvent()
    data object OnExamTimePickerClick : ExamScreenUIEvent()
    data class OnTimePicked(val time: Time) : ExamScreenUIEvent()
    data object OnTimePickedDismiss : ExamScreenUIEvent()
    data object OnExamSubjectPickerClick : ExamScreenUIEvent()
    data class OnSubjectPicked(val subject: Subject) : ExamScreenUIEvent()
    data object OnSubjectPickedDismiss : ExamScreenUIEvent()
    data object OnExamCategoryPickerClick : ExamScreenUIEvent()
    data class OnCategoryPicked(val category: ExamCategory) : ExamScreenUIEvent()
    data object OnCategoryPickedDismiss : ExamScreenUIEvent()
    data object OnExamAttachmentPickerClick : ExamScreenUIEvent()
    data class OnAttachmentPicked(val attachments: List<Attachment>) : ExamScreenUIEvent()
    data object OnAttachmentPickedDismiss : ExamScreenUIEvent()
    data object OnSaveExamButtonClick : ExamScreenUIEvent()
    data object OnSaveExamConfirmClick : ExamScreenUIEvent()
    data object OnSaveConfirmationDialogDismiss : ExamScreenUIEvent()
    data object OnExamSavedDialogDismiss : ExamScreenUIEvent()
    data object OnErrorDialogDismiss : ExamScreenUIEvent()
}