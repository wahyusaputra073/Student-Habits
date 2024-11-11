package com.wahyusembiring.lecture.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.LecturerWithSubject
import com.wahyusembiring.data.repository.LecturerRepository
import com.wahyusembiring.ui.util.UIText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LecturerScreenViewModel @Inject constructor(
    private val lecturerRepository: LecturerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LecturerScreenUIState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<LecturerScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            lecturerRepository.getAllLecturerWithSubjects().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update { it.copy(popUps = it.popUps + LecturerScreenPopUp.Loading) }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(LecturerScreenPopUp.Loading)
                                    .plus(
                                        LecturerScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown Error"))
                                    )
                            )
                        }
                    }
                    is Result.Success -> {
                        _state.update { it.copy(popUps = it.popUps.minus(LecturerScreenPopUp.Loading)) }
                        result.data.collect{ listOfLecturerWithSubjects ->
                            _state.update {
                                it.copy(listOfLecturerWithSubjects = listOfLecturerWithSubjects)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onUIEvent(event: LecturerScreenUIEvent) {
        when (event) {
            is LecturerScreenUIEvent.OnAddLecturerClick -> onAddLecturerClick()
            is LecturerScreenUIEvent.OnLecturerClick -> onLecturerClick(event.lecturerWithSubjects)
            is LecturerScreenUIEvent.OnDeleteLecturerClick -> onDeleteLecturerClick(event.lecturerWithSubjects)
            is LecturerScreenUIEvent.OnDeletePhoneNumberClick -> onDeletePhoneNumberClick(event.phoneNumber)
            is LecturerScreenUIEvent.OnDeleteLecturerConfirmed -> onDeleteLecturerConfirmed(event.lecturerWithSubjects)
            is LecturerScreenUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)
        }
    }

    private fun onDismissPopUp(popUp: LecturerScreenPopUp) {
        _state.update { it.copy(popUps = it.popUps.minus(popUp)) }
    }

    private fun onDeleteLecturerConfirmed(lecturerWithSubjects: LecturerWithSubject) {
        viewModelScope.launch {
            Log.d("LecturerViewModel", "Deleting lecturer with ID: ${lecturerWithSubjects.lecturer.id}")
            withContext(Dispatchers.IO) {
                lecturerRepository.deleteLecturer(lecturerWithSubjects.lecturer.id).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _state.update {
                                it.copy(popUps = it.popUps + LecturerScreenPopUp.Loading)
                            }
                        }
                        is Result.Error -> {
                            _state.update {
                                it.copy(
                                    popUps = it.popUps
                                        .minus(LecturerScreenPopUp.Loading)
                                        .plus(
                                            LecturerScreenPopUp.Error(UIText.DynamicString(result.throwable.message ?: "Unknown Error"))
                                        )
                                )
                            }
                        }
                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    popUps = it.popUps
                                        .minus(LecturerScreenPopUp.Loading)
                                        .plus(LecturerScreenPopUp.LecturerDeleted)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onLecturerClick(lecturerWithSubjects: LecturerWithSubject) {
        _navigationEvent.trySend(
            LecturerScreenNavigationEvent.NavigateToLecturerDetail(lecturerWithSubjects.lecturer.id)
        )
    }

    private fun onAddLecturerClick() {
        _navigationEvent.trySend(LecturerScreenNavigationEvent.NavigateToAddLecturer)
    }

    private fun onDeleteLecturerClick(lecturerWithSubjects: LecturerWithSubject) {
        _state.update {
            it.copy(popUps = it.popUps + LecturerScreenPopUp.DeleteLecturerConfirmation(lecturerWithSubjects))
        }
    }

    private fun onDeletePhoneNumberClick(phoneNumber: String) {
        viewModelScope.launch {
            Log.d("LecturerViewModel", "Deleting phone number: $phoneNumber")
            withContext(Dispatchers.IO) {
                lecturerRepository.deletePhoneNumber(phoneNumber).collect { result ->
                    when (result) {
                        is Result.Loading -> {}
                        is Result.Error -> { throw result.throwable }
                        is Result.Success -> {}
                    }
                }
            }
        }
    }
}

