package com.wahyusembiring.subject.screen.create

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.util.launch
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.repository.LecturerRepository
import com.wahyusembiring.data.repository.SubjectRepository
import com.wahyusembiring.subject.R
import com.wahyusembiring.ui.util.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val lecturerRepository: LecturerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateSubjectScreenUIState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            lecturerRepository.getAllLecturer().collect { lectures ->
                _state.update { it.copy(lectures = lectures) }
            }
        }
    }

    fun onUIEvent(event: CreateSubjectScreenUIEvent) {
        when (event) {
            is CreateSubjectScreenUIEvent.OnSubjectNameChanged -> updateSubjectName(event.name)
            is CreateSubjectScreenUIEvent.OnRoomChanged -> updateRoom(event.room)
            is CreateSubjectScreenUIEvent.OnSaveButtonClicked -> onSaveButtonClicked()
            is CreateSubjectScreenUIEvent.OnPickColorButtonClicked -> onPickColorButtonClicked()
            is CreateSubjectScreenUIEvent.OnLecturerSelected -> onLecturerSelected(event.lecturer)
            is CreateSubjectScreenUIEvent.OnColorPicked -> onColorPicked(event.color)
            is CreateSubjectScreenUIEvent.OnColorPickerDismiss -> onColorPickerDismiss()
            is CreateSubjectScreenUIEvent.OnErrorDialogDismiss -> onErrorDialogDismiss()
            is CreateSubjectScreenUIEvent.OnSaveConfirmationDialogConfirm -> onSaveConfirmationDialogConfirm()
            is CreateSubjectScreenUIEvent.OnSaveConfirmationDialogDismiss -> onSaveConfirmationDialogDismiss()
            is CreateSubjectScreenUIEvent.OnSubjectSavedDialogDismiss -> onSubjectSavedDialogDismiss()
        }
    }

    private fun onSubjectSavedDialogDismiss() {
        _state.update { it.copy(showSubjectSavedDialog = false) }
    }

    private fun onSaveConfirmationDialogDismiss() {
        _state.update { it.copy(showSaveConfirmationDialog = false) }
    }

    private fun onErrorDialogDismiss() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun onColorPickerDismiss() {
        _state.update { it.copy(showColorPicker = false) }
    }

    private fun onColorPicked(color: Color) {
        _state.update { it.copy(color = color) }
    }

    private fun onLecturerSelected(lecturer: Lecturer) {
        _state.update { it.copy(lecture = lecturer) }
    }

    private fun onPickColorButtonClicked() {
        _state.update { it.copy(showColorPicker = true) }
    }

    private fun onSaveButtonClicked() {
        _state.update { it.copy(showSaveConfirmationDialog = true) }
    }

    private fun onSaveConfirmationDialogConfirm() {
        _state.update { it.copy(showSavingLoading = true) }
        viewModelScope.launch {
            saveSubjectSafely()
        }
    }

    private suspend fun saveSubjectSafely() {
        try {
            saveSubject()
            _state.update {
                it.copy(
                    showSavingLoading = false,
                    showSubjectSavedDialog = true
                )
            }
        } catch (e: MissingRequiredFieldException) {
            handleMissingFieldException(e)
        }
    }

    private suspend fun saveSubject() {
        val subject = Subject(
            name = _state.value.name.ifBlank { throw MissingRequiredFieldException.SubjectName() },
            color = _state.value.color,
            room = _state.value.room.ifBlank { throw MissingRequiredFieldException.Room() },
            description = _state.value.description,
            lecturerId = _state.value.lecture?.id ?: throw MissingRequiredFieldException.Lecture()
        )
        subjectRepository.saveSubject(subject)
    }

    private fun handleMissingFieldException(e: MissingRequiredFieldException) {
        _state.update { it.copy(showSavingLoading = false) }
        val errorMessage = when (e) {
            is MissingRequiredFieldException.SubjectName -> UIText.StringResource(R.string.subject_name_is_required)
            is MissingRequiredFieldException.Room -> UIText.StringResource(R.string.room_is_required)
            is MissingRequiredFieldException.Lecture -> UIText.StringResource(R.string.please_select_a_lecture)
        }
        _state.update { it.copy(errorMessage = errorMessage) }
    }

    private fun updateSubjectName(name: String) {
        _state.update { it.copy(name = name) }
    }

    private fun updateRoom(room: String) {
        _state.update { it.copy(room = room) }
    }
}
