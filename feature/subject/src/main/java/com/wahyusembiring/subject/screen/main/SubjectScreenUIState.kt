package com.wahyusembiring.subject.screen.main

import com.wahyusembiring.data.model.SubjectWithExamAndHomework
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.ui.util.UIText

data class SubjectScreenUIState(
    val subjects: List<SubjectWithExamAndHomework> = emptyList(),
    val popUps: List<SubjectScreenPopUp> = emptyList()
)

sealed class SubjectScreenPopUp {
    data object Loading: SubjectScreenPopUp()
    data class DeleteSubjectConfirmation(val subject: Subject): SubjectScreenPopUp()
    data class Error(val errorMessage: UIText): SubjectScreenPopUp()
    data object SubjectDeleted: SubjectScreenPopUp()
}