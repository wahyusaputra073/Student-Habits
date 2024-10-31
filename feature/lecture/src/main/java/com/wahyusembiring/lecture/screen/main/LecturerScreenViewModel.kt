package com.wahyusembiring.lecture.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.data.model.LecturerWithSubject
import com.wahyusembiring.data.repository.LecturerRepository
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
            refreshLecturerList()
        }
    }

    fun onUIEvent(event: LecturerScreenUIEvent) {
        when (event) {
            is LecturerScreenUIEvent.OnAddLecturerClick -> onAddLecturerClick()
            is LecturerScreenUIEvent.OnLecturerClick -> onLecturerClick(event.lecturerWithSubjects)
            is LecturerScreenUIEvent.OnDeleteLecturerClick -> onDeleteLecturerClick(event.lecturerWithSubjects)
            is LecturerScreenUIEvent.OnDeletePhoneNumberClick -> onDeletePhoneNumberClick(event.phoneNumber)
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
        viewModelScope.launch {
            Log.d("LecturerViewModel", "Deleting lecturer with ID: ${lecturerWithSubjects.lecturer.id}")
            withContext(Dispatchers.IO) {
                lecturerRepository.deleteLecturer(lecturerWithSubjects.lecturer.id)
            }
            refreshLecturerList()
        }
    }

    private fun onDeletePhoneNumberClick(phoneNumber: String) {
        viewModelScope.launch {
            Log.d("LecturerViewModel", "Deleting phone number: $phoneNumber")
            withContext(Dispatchers.IO) {
                lecturerRepository.deletePhoneNumber(phoneNumber)
            }
            refreshLecturerList()
        }
    }

    private suspend fun refreshLecturerList() {
        Log.d("LecturerViewModel", "Refreshing lecturer list...")
        lecturerRepository.getAllLecturerWithSubjects().collect { listOfLecturerWithSubjects ->
            _state.update {
                it.copy(listOfLecturerWithSubjects = listOfLecturerWithSubjects)
            }
        }
        Log.d("LecturerViewModel", "Lecturer list refreshed.")
    }
}

