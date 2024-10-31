package com.wahyusembiring.homework

import android.content.Context
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.data.model.entity.Subject
import java.util.Date

sealed class CreateHomeworkUIEvent {
    data class OnHomeworkTitleChanged(val title: String) : CreateHomeworkUIEvent()
    data object OnSaveHomeworkButtonClicked : CreateHomeworkUIEvent()
    data object OnConfirmSaveHomeworkClick : CreateHomeworkUIEvent()
    data object OnPickDateButtonClicked : CreateHomeworkUIEvent()
    data class OnDatePicked(val date: Date) : CreateHomeworkUIEvent()
    data class OnTimePicked(val time: Time) : CreateHomeworkUIEvent()
    data class OnSubjectPicked(val subject: Subject) : CreateHomeworkUIEvent()
    data class OnAttachmentPicked(val attachments: List<Attachment>) : CreateHomeworkUIEvent()

    data object OnPickTimeButtonClicked : CreateHomeworkUIEvent()
    data object OnPickSubjectButtonClicked : CreateHomeworkUIEvent()
    data object OnPickAttachmentButtonClicked : CreateHomeworkUIEvent()

    //popup dismiss
    data object OnDismissDatePicker : CreateHomeworkUIEvent()
    data object OnDismissTimePicker : CreateHomeworkUIEvent()
    data object OnDismissSubjectPicker : CreateHomeworkUIEvent()
    data object OnDismissAttachmentPicker : CreateHomeworkUIEvent()
    data object OnDismissSaveConfirmationDialog : CreateHomeworkUIEvent()
    data object OnDismissHomeworkSavedDialog : CreateHomeworkUIEvent()
    data object OnDismissSavingLoading : CreateHomeworkUIEvent()
    data object OnDismissErrorDialog : CreateHomeworkUIEvent()
}