package com.wahyusembiring.lecture.screen.main

import androidx.navigation.NavController
import com.wahyusembiring.data.model.LecturerWithSubject

data class LecturerScreenUIState(
    val listOfLecturerWithSubjects: List<LecturerWithSubject> = emptyList()
)

sealed class LecturerScreenUIEvent {
    data class OnAddLecturerClick(val navController: NavController) : LecturerScreenUIEvent()
    data class OnLecturerClick(val lecturerWithSubjects: LecturerWithSubject) : LecturerScreenUIEvent()
    data class OnDeleteLecturerClick(val lecturerWithSubjects: LecturerWithSubject) : LecturerScreenUIEvent() // Event untuk menghapus lecturer
    data class OnDeletePhoneNumberClick(val phoneNumber: String) : LecturerScreenUIEvent() // Event untuk menghapus nomor telepon
}

sealed class LecturerScreenNavigationEvent {
    data class NavigateToLecturerDetail(val lecturerId: Int) : LecturerScreenNavigationEvent()
    data object NavigateToAddLecturer : LecturerScreenNavigationEvent()
}
