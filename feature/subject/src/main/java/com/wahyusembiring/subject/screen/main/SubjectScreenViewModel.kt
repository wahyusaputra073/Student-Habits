package com.wahyusembiring.subject.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.repository.EventRepository
import com.wahyusembiring.data.repository.SubjectRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubjectScreenViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            subjectRepository.getAllSubjectWithExamAndHomework()
                .collect { subjectWithExamAndHomework ->
                    println("Broooooo ${subjectWithExamAndHomework.size}")
                    _uiState.update {
                        it.copy(
                            subjects = subjectWithExamAndHomework
                        )
                    }
                }
        }
    }

    private val _uiState = MutableStateFlow(SubjectScreenUIState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<SubjectScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onUIEvent(event: SubjectScreenUIEvent) {
        when (event) {
            is SubjectScreenUIEvent.OnExamClick -> {}
            is SubjectScreenUIEvent.OnFloatingActionButtonClick -> {}
            is SubjectScreenUIEvent.OnHamburgerMenuClick -> {}
            is SubjectScreenUIEvent.OnHomeworkClick -> {}
            is SubjectScreenUIEvent.OnSubjectClick -> onSubjectClick(event.subject)
        }
    }

    private fun onSubjectClick(subject: Subject) {
        _navigationEvent.trySend(SubjectScreenNavigationEvent.NavigateToSubjectDetail(subject))
    }


}