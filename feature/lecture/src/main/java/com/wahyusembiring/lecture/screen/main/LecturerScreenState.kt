package com.wahyusembiring.lecture.screen.main

import androidx.navigation.NavController
import com.wahyusembiring.data.model.LecturerWithSubject
import com.wahyusembiring.ui.util.UIText

data class LecturerScreenUIState(
    val listOfLecturerWithSubjects: List<LecturerWithSubject> = emptyList(),
    val popUps: List<LecturerScreenPopUp> = emptyList()
)

sealed class LecturerScreenPopUp {
    data object Loading : LecturerScreenPopUp()
    data class Error(val message: UIText) : LecturerScreenPopUp()
    data class DeleteLecturerConfirmation(val lecturerWithSubjects: LecturerWithSubject) : LecturerScreenPopUp()
    data object LecturerDeleted : LecturerScreenPopUp()
}

sealed class LecturerScreenUIEvent {
    data object OnAddLecturerClick : LecturerScreenUIEvent()
    data class OnLecturerClick(val lecturerWithSubjects: LecturerWithSubject) : LecturerScreenUIEvent()
    data class OnDeleteLecturerClick(val lecturerWithSubjects: LecturerWithSubject) : LecturerScreenUIEvent() // Event untuk menghapus lecturer
    data class OnDeleteLecturerConfirmed(val lecturerWithSubjects: LecturerWithSubject) : LecturerScreenUIEvent()
    data class OnDeletePhoneNumberClick(val phoneNumber: String) : LecturerScreenUIEvent() // Event untuk menghapus nomor telepon
    data class OnDismissPopUp(val popUp: LecturerScreenPopUp) : LecturerScreenUIEvent()
}

sealed class LecturerScreenNavigationEvent {
    data class NavigateToLecturerDetail(val lecturerId: String) : LecturerScreenNavigationEvent()
    data object NavigateToAddLecturer : LecturerScreenNavigationEvent()
}
