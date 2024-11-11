package com.wahyusembiring.subject.screen.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.data.repository.EventRepository
import com.wahyusembiring.data.repository.SubjectRepository
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
class SubjectScreenViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubjectScreenUIState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<SubjectScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _oneTimeEvent = Channel<SubjectScreenUIEvent>()
    val oneTimeEvent = _oneTimeEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            subjectRepository.getAllSubjectWithExamAndHomework()
                .collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.update {
                                it.copy(popUps = it.popUps + SubjectScreenPopUp.Loading)
                            }
                        }
                        is Result.Error -> { throw result.throwable }
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(popUps = it.popUps - SubjectScreenPopUp.Loading)
                            }
                            result.data.collect { subjectWithExamAndHomework ->
                                _uiState.update {
                                    it.copy(
                                        subjects = subjectWithExamAndHomework
                                    )
                                }
                            }
                        }
                    }
                }
        }
    }



    fun onUIEvent(event: SubjectScreenUIEvent) {
        when (event) {
            is SubjectScreenUIEvent.OnFloatingActionButtonClick -> onFloatingActionButtonClick()
            is SubjectScreenUIEvent.OnHamburgerMenuClick -> onHamburgerMenuClick()
            is SubjectScreenUIEvent.OnSubjectClick -> onSubjectClick(event.subject)
            is SubjectScreenUIEvent.OnDeleteSubjectClick -> onDeleteSubjectClick(event.subject)
            is SubjectScreenUIEvent.OnSubjectDeleteConfirmed -> onSubjectDeleteConfirmed(event.subject)
            is SubjectScreenUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)
        }
    }

    private fun onDismissPopUp(popUp: SubjectScreenPopUp) {
        _uiState.update {
            it.copy(popUps = it.popUps - popUp)
        }
    }

    private fun onSubjectDeleteConfirmed(subject: Subject) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                subjectRepository.onDeleteSubject(subject).collect { result ->
                    when (result) {
                        is Result.Loading -> {
                            _uiState.update {
                                it.copy(popUps = it.popUps + SubjectScreenPopUp.Loading)
                            }
                        }
                        is Result.Error -> { throw result.throwable }
                        is Result.Success -> {
                            _uiState.update {
                                it.copy(
                                    popUps = it.popUps
                                        .minus(SubjectScreenPopUp.Loading)
                                        .plus(SubjectScreenPopUp.SubjectDeleted)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun onHamburgerMenuClick() {
        _oneTimeEvent.trySend(SubjectScreenUIEvent.OnHamburgerMenuClick)
    }

    private fun onFloatingActionButtonClick() {
        _navigationEvent.trySend(SubjectScreenNavigationEvent.NavigateToCreateSubject)
    }

    private fun onSubjectClick(subject: Subject) {
        _navigationEvent.trySend(SubjectScreenNavigationEvent.NavigateToSubjectDetail(subject))
    }

    private fun onDeleteSubjectClick(subject: Subject) {
        _uiState.update {
            it.copy(popUps = it.popUps + SubjectScreenPopUp.DeleteSubjectConfirmation(subject))
        }
    }


}