package com.wahyusembiring.subject.screen.create

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.ui.util.UIText

sealed class CreateSubjectScreenUIEvent {
    data object OnSaveButtonClicked : CreateSubjectScreenUIEvent()
    data class OnSubjectNameChanged(val name: String) : CreateSubjectScreenUIEvent()
    data class OnRoomChanged(val room: String) : CreateSubjectScreenUIEvent()
    data object OnPickColorButtonClicked : CreateSubjectScreenUIEvent()
    data class OnColorPicked(val color: Color) : CreateSubjectScreenUIEvent()
    data class OnLecturerSelected(val lecturer: Lecturer) : CreateSubjectScreenUIEvent()
    data object OnSaveConfirmationDialogConfirm : CreateSubjectScreenUIEvent()
    data object OnNavigateUpButtonClicked : CreateSubjectScreenUIEvent()
    data class OnDescriptionChanged(val description: String) : CreateSubjectScreenUIEvent()
    data object OnPickLecturerButtonClicked : CreateSubjectScreenUIEvent()
    data class OnDismissPopup(val popup: CreateSubjectScreenPopUp) : CreateSubjectScreenUIEvent()
    data object OnAddNewLecturerButtonClicked : CreateSubjectScreenUIEvent()
}

sealed class CreateSubjectScreenPopUp {
    data object ColorPicker: CreateSubjectScreenPopUp()
    data object LecturerPicker: CreateSubjectScreenPopUp()
    data object Loading: CreateSubjectScreenPopUp()
    data class Error(val errorMessage: UIText): CreateSubjectScreenPopUp()
    data object SaveConfirmation: CreateSubjectScreenPopUp()
    data object SubjectSaved: CreateSubjectScreenPopUp()
}

sealed class CreateSubjectScreenOneTimeEvent {
    data object NavigateUp : CreateSubjectScreenOneTimeEvent()
    data object NavigateToCreateLecturer : CreateSubjectScreenOneTimeEvent()
}
