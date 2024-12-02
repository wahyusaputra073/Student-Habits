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
    val popUps: List<CreateSubjectScreenPopUp> = emptyList()
)
