package com.wahyusembiring.subject.screen.create

import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.wahyusembiring.data.model.entity.Lecturer

sealed class CreateSubjectScreenUIEvent {
    data object OnSaveButtonClicked : CreateSubjectScreenUIEvent()
    data class OnSubjectNameChanged(val name: String) : CreateSubjectScreenUIEvent()
    data class OnRoomChanged(val room: String) : CreateSubjectScreenUIEvent()
    data object OnPickColorButtonClicked : CreateSubjectScreenUIEvent()
    data class OnColorPicked(val color: Color) : CreateSubjectScreenUIEvent()
    data object OnColorPickerDismiss : CreateSubjectScreenUIEvent()
    data class OnLecturerSelected(val lecturer: Lecturer) : CreateSubjectScreenUIEvent()
    data object OnSaveConfirmationDialogConfirm : CreateSubjectScreenUIEvent()
    data object OnSaveConfirmationDialogDismiss : CreateSubjectScreenUIEvent()
    data object OnSubjectSavedDialogDismiss : CreateSubjectScreenUIEvent()
    data object OnErrorDialogDismiss : CreateSubjectScreenUIEvent()
}
