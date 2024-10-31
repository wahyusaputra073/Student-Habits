package com.wahyusembiring.overview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.wahyusembiring.ui.component.eventcard.EventCard
import com.wahyusembiring.ui.component.scoredialog.ScoreDialog

data class OverviewScreenUIState(
    val eventCards: List<EventCard> = emptyList(),
    val scoreDialog: ScoreDialog? = null
)