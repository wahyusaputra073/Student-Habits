package com.wahyusembiring.thesisplanner.screen.thesisselection

import com.wahyusembiring.data.model.ThesisWithTask

typealias Thesis = ThesisWithTask

data class ThesisSelectionScreenUIState(
    val listOfThesis: List<Thesis> = emptyList()
)