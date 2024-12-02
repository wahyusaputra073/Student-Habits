package com.wahyusembiring.subject.screen.create

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.entity.Lecturer
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.repository.LecturerRepository
import com.wahyusembiring.data.repository.SubjectRepository
import com.wahyusembiring.subject.R
import com.wahyusembiring.ui.util.UIText
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = CreateSubjectViewModel.Factory::class,
)
class CreateSubjectViewModel @AssistedInject constructor(
    private val subjectRepository: SubjectRepository,
    private val lecturerRepository: LecturerRepository,
    @Assisted private val subjectId: String
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(subjectId: String = "-1"): CreateSubjectViewModel
    }

    private val _state = MutableStateFlow(CreateSubjectScreenUIState())
    val state = _state.asStateFlow()

    private val _oneTimeEvent: Channel<CreateSubjectScreenOneTimeEvent> = Channel()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()

    init {
        if (subjectId != "-1") {
            _state.update { it.copy(isEditMode = true) }
        }
        viewModelScope.launch {
            launch {
                lecturerRepository.getAllLecturer().collect { result ->
                    when (result) {
                        is Result.Loading -> {}
                        is Result.Error -> { throw result.throwable }
                        is Result.Success -> {
                            result.data.collect { lectures ->
                                _state.update { it.copy(lecturers = lectures) }
                            }
                        }
                    }
                }
            }
            launch {
                if (subjectId != "-1") {
                    subjectRepository.getSubjectWithLecturerById(subjectId).collect { result ->
                        when (result) {
                            is Result.Loading -> {}
                            is Result.Error -> { throw result.throwable }
                            is Result.Success -> {
                                result.data.collect { subjectWithLecturer ->
                                    _state.update {
                                        it.copy(
                                            name = subjectWithLecturer?.subject?.name ?: "",
                                            color = subjectWithLecturer?.subject?.color ?: Color.Unspecified,
                                            room = subjectWithLecturer?.subject?.room ?: "",
                                            description = subjectWithLecturer?.subject?.description ?: "",
                                            lecturer = subjectWithLecturer?.lecturer,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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
            is CreateSubjectScreenUIEvent.OnSaveConfirmationDialogConfirm -> onSaveConfirmationDialogConfirm()
            is CreateSubjectScreenUIEvent.OnDescriptionChanged -> onDescriptionChanged(event.description)
            is CreateSubjectScreenUIEvent.OnNavigateUpButtonClicked -> onNavigateUpButtonClicked()
            is CreateSubjectScreenUIEvent.OnPickLecturerButtonClicked -> onPickLecturerButtonClicked()
            is CreateSubjectScreenUIEvent.OnDismissPopup -> onDismissPopup(event.popup)
            is CreateSubjectScreenUIEvent.OnAddNewLecturerButtonClicked -> onAddNewLecturerButtonClicked()
        }
    }

    private fun onAddNewLecturerButtonClicked() {
        _oneTimeEvent.trySend(CreateSubjectScreenOneTimeEvent.NavigateToCreateLecturer)
    }

    private fun onDismissPopup(popup: CreateSubjectScreenPopUp) {
        _state.update {
            it.copy(popUps = it.popUps - popup)
        }
    }

    private fun onPickLecturerButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + CreateSubjectScreenPopUp.LecturerPicker)
        }
    }

    private fun onNavigateUpButtonClicked() {
        _oneTimeEvent.trySend(CreateSubjectScreenOneTimeEvent.NavigateUp)
    }

    private fun onDescriptionChanged(description: String) {
        _state.update {
            it.copy(description = description)
        }
    }

    private fun onColorPicked(color: Color) {
        _state.update { it.copy(color = color) }
    }

    private fun onLecturerSelected(lecturer: Lecturer) {
        _state.update { it.copy(lecturer = lecturer) }
    }

    private fun onPickColorButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + CreateSubjectScreenPopUp.ColorPicker)
        }
    }

    private fun onSaveButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + CreateSubjectScreenPopUp.SaveConfirmation)
        }
    }

    private fun onSaveConfirmationDialogConfirm() {
        viewModelScope.launch {
            try {
                saveSubject()
            } catch (e: MissingRequiredFieldException) {
                handleMissingFieldException(e)
            }
        }
    }

    private suspend fun saveSubject() {
        val subject = Subject(
            name = _state.value.name.ifBlank { throw MissingRequiredFieldException.SubjectName() },
            color = _state.value.color,
            room = _state.value.room.ifBlank { throw MissingRequiredFieldException.Room() },
            description = _state.value.description,
            lecturerId = _state.value.lecturer?.id ?: throw MissingRequiredFieldException.Lecture()
        )
        if (state.value.isEditMode) {
            subjectRepository.updateSubject(subject.copy(id = subjectId)).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + CreateSubjectScreenPopUp.Loading)
                        }
                    }
                    is Result.Error -> { throw result.throwable }
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CreateSubjectScreenPopUp.Loading)
                                    .plus(CreateSubjectScreenPopUp.SubjectSaved)
                            )
                        }
                    }
                }
            }
        } else {
            subjectRepository.saveSubject(subject).collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + CreateSubjectScreenPopUp.Loading)
                        }
                    }
                    is Result.Error -> { throw result.throwable }
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(CreateSubjectScreenPopUp.Loading)
                                    .plus(CreateSubjectScreenPopUp.SubjectSaved)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun handleMissingFieldException(e: MissingRequiredFieldException) {
        val errorMessage = when (e) {
            is MissingRequiredFieldException.SubjectName -> UIText.StringResource(R.string.subject_name_is_required)
            is MissingRequiredFieldException.Room -> UIText.StringResource(R.string.room_is_required)
            is MissingRequiredFieldException.Lecture -> UIText.StringResource(R.string.please_select_a_lecture)
        }
        _state.update {
            it.copy(popUps = it.popUps + CreateSubjectScreenPopUp.Error(errorMessage))
        }
    }

    private fun updateSubjectName(name: String) {
        _state.update { it.copy(name = name) }
    }

    private fun updateRoom(room: String) {
        _state.update { it.copy(room = room) }
    }
}
