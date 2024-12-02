package com.wahyusembiring.exam

import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.ui.ReminderOption
import com.wahyusembiring.ui.util.UIText
import java.time.LocalDateTime

data class ExamScreenUIState(
    val isEditMode: Boolean = false,
    val examTitle: String = "",
    val dueDate: LocalDateTime = LocalDateTime.now(),
    val dueReminder: ReminderOption? = ReminderOption.dueReminderDefaultOptions.first(),
    val deadline: LocalDateTime = dueDate.plusWeeks(1),
    val deadlineReminder: ReminderOption? = ReminderOption.deadlineReminderDefaultOptions.first(),
    val subjects: List<Subject> = emptyList(),
    val subject: Subject? = null,
    val notes: String = "",
    val score: Int? = null,
    val category: ExamCategory = ExamCategory.WRITTEN,
    val popUps: List<CreateExamScreenPopUp> = emptyList(),
)

sealed class CreateExamScreenPopUp {
    data object DuePeriodPicker : CreateExamScreenPopUp()
    data object CustomDueReminderPicker : CreateExamScreenPopUp()
    data object CustomDeadlineReminderPicker : CreateExamScreenPopUp()
    data object SubjectPicker : CreateExamScreenPopUp()
    data object CustomExamCategoryPicker : CreateExamScreenPopUp()
    data object SaveConfirmationDialog : CreateExamScreenPopUp()
    data object ExamSavedDialog : CreateExamScreenPopUp()
    data object Loading : CreateExamScreenPopUp()
    data class Error(val errorMessage: UIText) : CreateExamScreenPopUp()
    data object ScoreInputDialog : CreateExamScreenPopUp()
}

sealed class ExamScreenUIEvent {
    data class OnExamNameChanged(val name: String) : ExamScreenUIEvent()
    data object OnPickExamPeriodButtonClicked : ExamScreenUIEvent()
    data object OnCustomExamDayReminderButtonClicked : ExamScreenUIEvent()
    data class OnExamDayReminderChanged(val reminder: ReminderOption) : ExamScreenUIEvent()
    data object OnCustomExamDeadlineReminderButtonClicked : ExamScreenUIEvent()
    data class OnExamDeadlineReminderChanged(val reminder: ReminderOption) : ExamScreenUIEvent()
    data class OnExamNotesChanged(val notes: String) : ExamScreenUIEvent()
    data object OnExamSubjectPickerClick : ExamScreenUIEvent()
    data class OnSubjectPicked(val subject: Subject) : ExamScreenUIEvent()
    data object OnExamCategoryPickerClick : ExamScreenUIEvent()
    data class OnCategoryPicked(val category: ExamCategory) : ExamScreenUIEvent()
    data object OnSaveExamButtonClick : ExamScreenUIEvent()
    data object OnSaveExamConfirmClick : ExamScreenUIEvent()
    data class OnDismissPopUp(val popUp: CreateExamScreenPopUp) : ExamScreenUIEvent()
    data object OnNavigateBackRequest : ExamScreenUIEvent()
    data object OnNavigateToSubjectScreenRequest : ExamScreenUIEvent()
    data class OnExamScoreChanged(val score: Int?) : ExamScreenUIEvent()
}

sealed class CreateExamScreenNavigationEvent {
    data object NavigateBack : CreateExamScreenNavigationEvent()
    data object NavigateToCreateSubject : CreateExamScreenNavigationEvent()
}