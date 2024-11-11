package com.wahyusembiring.exam

import android.net.Uri
import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.model.Time
import com.wahyusembiring.ui.util.UIText
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date

data class ExamScreenUIState(
    val isEditMode: Boolean = false,
    val name: String = "",
    val description: String = "",
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val subjects: List<Subject> = emptyList(),
    val subject: Subject? = null,
    val score: Int? = null,
    val category: ExamCategory = ExamCategory.WRITTEN,
    val attachments: List<Attachment> = emptyList(),
    val popUps: List<CreateExamScreenPopUp> = emptyList(),
)

sealed class CreateExamScreenPopUp {
    data object DatePicker : CreateExamScreenPopUp()
    data object TimePicker : CreateExamScreenPopUp()
    data object SubjectPicker : CreateExamScreenPopUp()
    data object AttachmentPicker : CreateExamScreenPopUp()
    data object ExamCategoryPicker : CreateExamScreenPopUp()
    data object SaveConfirmationDialog : CreateExamScreenPopUp()
    data object ExamSavedDialog : CreateExamScreenPopUp()
    data object Loading : CreateExamScreenPopUp()
    data class Error(val errorMessage: UIText) : CreateExamScreenPopUp()
}

sealed class ExamScreenUIEvent {
    data class OnExamNameChanged(val name: String) : ExamScreenUIEvent()
    data class OnExamDescriptionChanged(val name: String) : ExamScreenUIEvent()
    data object OnExamDatePickerClick : ExamScreenUIEvent()
    data class OnDatePicked(val date: LocalDate) : ExamScreenUIEvent()
    data object OnExamTimePickerClick : ExamScreenUIEvent()
    data class OnTimePicked(val time: LocalTime) : ExamScreenUIEvent()
    data object OnExamSubjectPickerClick : ExamScreenUIEvent()
    data class OnSubjectPicked(val subject: Subject) : ExamScreenUIEvent()
    data object OnExamCategoryPickerClick : ExamScreenUIEvent()
    data class OnCategoryPicked(val category: ExamCategory) : ExamScreenUIEvent()
    data object OnExamAttachmentPickerClick : ExamScreenUIEvent()
    data class OnAttachmentPicked(val attachments: List<Attachment>) : ExamScreenUIEvent()
    data object OnSaveExamButtonClick : ExamScreenUIEvent()
    data object OnSaveExamConfirmClick : ExamScreenUIEvent()
    data class OnDismissPopUp(val popUp: CreateExamScreenPopUp) : ExamScreenUIEvent()
    data object OnNavigateBackRequest : ExamScreenUIEvent()
    data object OnNavigateToSubjectScreenRequest : ExamScreenUIEvent()
}

sealed class CreateExamScreenNavigationEvent {
    data object NavigateBack : CreateExamScreenNavigationEvent()
    data object NavigateToCreateSubject : CreateExamScreenNavigationEvent()
}