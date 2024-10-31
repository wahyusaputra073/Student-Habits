package com.wahyusembiring.thesisplanner.screen.thesisselection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.launch
import com.wahyusembiring.data.repository.ThesisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThesisSelectionScreenViewModel @Inject constructor(
    private val thesisRepository: ThesisRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            thesisRepository.getAllThesis().collect { listOfThesis ->
                _uiState.update {
                    it.copy(listOfThesis = listOfThesis)
                }
            }
        }
    }

    private val _uiState = MutableStateFlow(ThesisSelectionScreenUIState())
    val uiState = _uiState.asStateFlow()

    fun onUIEvent(event: ThesisSelectionScreenUIEvent) {
        when (event) {
            is ThesisSelectionScreenUIEvent.OnCreateNewThesisClick -> launch {
                onCreateNewThesisClick(
                    event.onNavigateToThesisPlanner
                )
            }

            is ThesisSelectionScreenUIEvent.OnDeleteThesisClick -> launch {
                onDeleteThesisClick(
                    event.thesis
                )
            }
        }
    }


    private suspend fun onDeleteThesisClick(thesis: Thesis) {
        thesisRepository.deleteThesis(thesis.thesis)
    }

    private suspend fun onCreateNewThesisClick(onNavigateToThesisPlanner: (thesisId: Int) -> Unit) {
        val newThesis = com.wahyusembiring.data.model.entity.Thesis(
            title = "Untitled Thesis",
            articles = emptyList()
        )
        val newThesisId = thesisRepository.saveNewThesis(newThesis)
        onNavigateToThesisPlanner(newThesisId.toInt())
    }

}