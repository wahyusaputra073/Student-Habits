package com.wahyusembiring.subject.screen.create

import androidx.compose.ui.graphics.Color
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.ui.theme.primaryLight
import com.wahyusembiring.ui.util.UIText

data class CreateSubjectScreenUIState(
    val isEditMode: Boolean = false,
    val name: String = "",
    val color: Color = primaryLight,
    val room: String = "",
    val description: String = "",
    val lecturer: Lecturer? = null,
    val lecturers: List<Lecturer> = emptyList(),

    //popup
    val showColorPicker: Boolean = false,
    val showSaveConfirmationDialog: Boolean = false,
    val showSavingLoading: Boolean = false,
    val showSubjectSavedDialog: Boolean = false,
    val errorMessage: UIText? = null
)
