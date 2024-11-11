package com.wahyusembiring.homework

import android.net.Uri
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.DeadlineTime
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.ui.util.UIText
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

data class CreateHomeworkScreenUIState(
    val isEditMode: Boolean = false,
    val homeworkTitle: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val subjects: List<Subject> = emptyList(),
    val subject: Subject? = null,
    val attachments: List<Attachment> = emptyList(),
    val isCompleted: Boolean = false,
    val description: String = "",
    val popUps: List<CreateHomeworkScreenPopUp> = emptyList(),
)

sealed class CreateHomeworkScreenPopUp {
    data object Loading : CreateHomeworkScreenPopUp()
    data class Error(val errorMessage: UIText) : CreateHomeworkScreenPopUp()
    data object DatePicker : CreateHomeworkScreenPopUp()
    data object TimePicker : CreateHomeworkScreenPopUp()
    data object SubjectPicker : CreateHomeworkScreenPopUp()
    data object AttachmentPicker : CreateHomeworkScreenPopUp()
    data object SaveConfirmationDialog : CreateHomeworkScreenPopUp()
    data object HomeworkSavedDialog : CreateHomeworkScreenPopUp()
}

sealed class CreateHomeworkUIEvent {
    data class OnHomeworkTitleChanged(val title: String) : CreateHomeworkUIEvent()
    data class OnExamDescriptionChanged(val title: String) : CreateHomeworkUIEvent()
    data object OnSaveHomeworkButtonClicked : CreateHomeworkUIEvent()
    data object OnConfirmSaveHomeworkClick : CreateHomeworkUIEvent()
    data object OnHomeworkSavedButtonClicked : CreateHomeworkUIEvent()
    data object OnPickDateButtonClicked : CreateHomeworkUIEvent()
    data class OnDatePicked(val date: LocalDate) : CreateHomeworkUIEvent()
    data class OnTimePicked(val time: LocalTime) : CreateHomeworkUIEvent()
    data class OnSubjectPicked(val subject: Subject) : CreateHomeworkUIEvent()
    data class OnAttachmentPicked(val attachments: List<Attachment>) : CreateHomeworkUIEvent()
    data object OnPickTimeButtonClicked : CreateHomeworkUIEvent()
    data object OnPickSubjectButtonClicked : CreateHomeworkUIEvent()
    data object OnPickAttachmentButtonClicked : CreateHomeworkUIEvent()
    data class OnDismissPopUp(val popUp: CreateHomeworkScreenPopUp) : CreateHomeworkUIEvent()
    data object OnNavigateToSubjectScreenRequest : CreateHomeworkUIEvent()
    data object OnNavigateBackButtonClick : CreateHomeworkUIEvent()
}

sealed class CreateHomeworkScreenNavigationEvent {
    data object NavigateBack : CreateHomeworkScreenNavigationEvent()
    data object NavigateToCreateSubject : CreateHomeworkScreenNavigationEvent()
}