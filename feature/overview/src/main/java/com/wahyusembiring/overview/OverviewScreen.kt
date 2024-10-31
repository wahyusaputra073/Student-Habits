package com.wahyusembiring.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.ui.component.eventcard.EventCard
import com.wahyusembiring.ui.component.scoredialog.ScoreDialog
import com.wahyusembiring.ui.component.floatingactionbutton.HomeworkExamAndReminderFAB
import com.wahyusembiring.ui.component.topappbar.TopAppBar
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch


@Composable
fun OverviewScreen(
    viewModel: OverviewViewModel,
    navController: NavHostController,
    drawerState: DrawerState
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    OverviewScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent,
        onHamburgerMenuClick = {
            coroutineScope.launch { drawerState.open() }
        },
        onNavigateTo = {
            navController.navigate(it)
        }
    )
}


@Composable
private fun OverviewScreen(
    modifier: Modifier = Modifier,
    state: OverviewScreenUIState,
    onUIEvent: (OverviewScreenUIEvent) -> Unit,
    onHamburgerMenuClick: () -> Unit,
    onNavigateTo: (Screen) -> Unit,
) {
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.overview),
                onMenuClick = onHamburgerMenuClick
            )
        },
        floatingActionButton = {
            HomeworkExamAndReminderFAB(
                isExpanded = fabExpanded,
                onClick = { fabExpanded = !fabExpanded },
                onDismiss = { fabExpanded = false },
                onReminderFabClick = { onNavigateTo(Screen.CreateReminder()) },
                onExamFabClick = { onNavigateTo(Screen.CreateExam()) },
                onHomeworkFabClick = { onNavigateTo(Screen.CreateHomework()) },
            )
        }
    ) {
        OverviewScreenMainContent(
            modifier = modifier.padding(it),
            state = state,
            onUIEvent = onUIEvent,
            onNavigateTo = onNavigateTo
        )
    }
}


@Suppress("t")
@Composable
private fun OverviewScreenMainContent(
    modifier: Modifier = Modifier,
    state: OverviewScreenUIState,
    onUIEvent: (OverviewScreenUIEvent) -> Unit,
    onNavigateTo: (Screen) -> Unit,
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = state.eventCards,
            key = { it.title.asString(context) }
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            DaySectionHeader(
                title = it.title.asString(),
                date = it.date.asString()
            )
            EventCard(
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.Large,
                    vertical = MaterialTheme.spacing.Small
                ),
                onEventClick = { event ->
                    when (event) {
                        is HomeworkWithSubject -> {
                            onNavigateTo(Screen.CreateHomework(event.homework.id))
                        }

                        is ExamWithSubject -> {
                            onNavigateTo(Screen.CreateExam(event.exam.id))
                        }

                        is Reminder -> {
                            onNavigateTo(Screen.CreateReminder(event.id))
                        }

                        else -> Unit
                    }
                },
                onDeletedEventClick = { event ->
                    onUIEvent(OverviewScreenUIEvent.OnDeleteEvent(event))
                },
                events = it.events,
                onEventCheckedChange = { event, isChecked ->
                    onUIEvent(OverviewScreenUIEvent.OnEventCompletedStateChange(event, isChecked))
                },
            )
        }
    }
    if (state.scoreDialog != null) {
        ScoreDialog(
            initialScore = state.scoreDialog.initialScore,
            onMarkNotDoneYet = {
                onUIEvent(
                    OverviewScreenUIEvent.OnMarkExamAsUndone(state.scoreDialog.exam)
                )
            },
            onDismissRequest = {
                onUIEvent(
                    OverviewScreenUIEvent
                        .OnExamScoreDialogStateChange(null)
                )
            },
            onScoreConfirmed = {
                onUIEvent(
                    OverviewScreenUIEvent.OnExamScorePicked(state.scoreDialog.exam, it)
                )
            }
        )
    }
}

@Composable
private fun DaySectionHeader(
    title: String,
    date: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.spacing.Medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.tertiary
        )
        Text(
            text = date,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
