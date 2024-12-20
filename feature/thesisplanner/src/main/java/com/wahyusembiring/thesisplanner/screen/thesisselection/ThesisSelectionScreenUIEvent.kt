package com.wahyusembiring.thesisplanner.screen.thesisselection

import androidx.navigation.NavHostController

sealed class ThesisSelectionScreenUIEvent {
    data class OnCreateNewThesisClick(val onNavigateToThesisPlanner: (thesisId: String) -> Unit) :
        ThesisSelectionScreenUIEvent()

    data class OnDeleteThesisClick(val thesis: Thesis) : ThesisSelectionScreenUIEvent()
}