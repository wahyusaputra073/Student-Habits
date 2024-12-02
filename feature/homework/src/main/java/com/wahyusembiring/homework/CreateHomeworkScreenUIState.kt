package com.wahyusembiring.homework

import com.wahyusembiring.data.model.Attachment
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.ui.ReminderOption
import com.wahyusembiring.ui.util.UIText
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.temporal.TemporalAmount

data class CreateHomeworkScreenUIState(
    val isEditMode: Boolean = false,
    val homeworkTitle: String = "",
    val dueDate: LocalDateTime = LocalDateTime.now(),
    val dueReminder: ReminderOption? = ReminderOption.dueReminderDefaultOptions.first(),
    val deadline: LocalDateTime = dueDate.plusWeeks(1),
    val deadlineReminder: ReminderOption? = ReminderOption.deadlineReminderDefaultOptions.first(),
    val subjects: List<Subject> = emptyList(),
    val subject: Subject? = null,
    val isCompleted: Boolean = false,
    val notes: String = "",
    val popUps: List<CreateHomeworkScreenPopUp> = emptyList(),
)

sealed class CreateHomeworkScreenPopUp {
    data object Loading : CreateHomeworkScreenPopUp()
    data class Error(val errorMessage: UIText) : CreateHomeworkScreenPopUp()
    data object DuePeriodPicker : CreateHomeworkScreenPopUp()
    data object DueReminderPicker : CreateHomeworkScreenPopUp()
    data object DeadlineReminderPicker : CreateHomeworkScreenPopUp()
    data object SubjectPicker : CreateHomeworkScreenPopUp()
    data object SaveConfirmationDialog : CreateHomeworkScreenPopUp()
    data object HomeworkSavedDialog : CreateHomeworkScreenPopUp()
}

sealed class CreateHomeworkUIEvent {
    data class OnHomeworkTitleChanged(val title: String) : CreateHomeworkUIEvent()
    data object OnPickDuePeriodButtonClicked : CreateHomeworkUIEvent()
    data class OnDueDateChanged(val dueDate: LocalDateTime): CreateHomeworkUIEvent()
    data class OnDeadlineDateChanged(val deadlineDate: LocalDateTime) : CreateHomeworkUIEvent()
    data class OnDueReminderChanged(val reminderOption: ReminderOption) : CreateHomeworkUIEvent()
    data object OnCustomDueReminderButtonClicked : CreateHomeworkUIEvent()
    data object OnCustomDeadlineReminderButtonClicked : CreateHomeworkUIEvent()
    data class OnDeadlineReminderChanged(val reminderOption: ReminderOption) : CreateHomeworkUIEvent()
    data class OnNotesChanged(val notes: String) : CreateHomeworkUIEvent()
    data class OnTaskCompletedStatusChanged(val isCompleted: Boolean) : CreateHomeworkUIEvent()
    data object OnSaveHomeworkButtonClicked : CreateHomeworkUIEvent()
    data object OnConfirmSaveHomeworkClick : CreateHomeworkUIEvent()
    data object OnHomeworkSavedButtonClicked : CreateHomeworkUIEvent()
    data class OnSubjectPicked(val subject: Subject) : CreateHomeworkUIEvent()
    data object OnPickSubjectButtonClicked : CreateHomeworkUIEvent()
    data class OnDismissPopUp(val popUp: CreateHomeworkScreenPopUp) : CreateHomeworkUIEvent()
    data object OnNavigateToSubjectScreenRequest : CreateHomeworkUIEvent()
    data object OnNavigateBackButtonClick : CreateHomeworkUIEvent()
}

sealed class CreateHomeworkScreenNavigationEvent {
    data object NavigateBack : CreateHomeworkScreenNavigationEvent()
    data object NavigateToCreateSubject : CreateHomeworkScreenNavigationEvent()
}