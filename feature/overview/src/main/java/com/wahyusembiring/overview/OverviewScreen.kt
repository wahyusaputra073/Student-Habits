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
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.ui.component.eventcard.EventCard
import com.wahyusembiring.ui.component.scoredialog.ScoreDialog
import com.wahyusembiring.ui.component.floatingactionbutton.HomeworkExamAndReminderFAB
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
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

    CollectAsOneTimeEvent(viewModel.navigationEvent) {  event ->
        when (event) {
            is OverviewScreenNavigationEvent.NavigateToExam -> {
                navController.navigate(Screen.CreateExam(event.examId ?: "-1"))
            }
            is OverviewScreenNavigationEvent.NavigateToHomework -> {
                navController.navigate(Screen.CreateHomework(event.homeworkId ?: "-1"))
            }
            is OverviewScreenNavigationEvent.NavigateToReminder -> {
                navController.navigate(Screen.CreateReminder(event.reminderId ?: "-1"))
            }
        }
    }

    CollectAsOneTimeEvent(viewModel.oneTimeEvent) { event ->
        when (event) {
            is OverviewScreenUIEvent.OnHamburgerMenuClick -> {
                coroutineScope.launch {
                    drawerState.open()
                }
            }
            else -> Unit
        }
    }

    OverviewScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent,
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is OverviewScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.errorMessage,
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(OverviewScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(OverviewScreenUIEvent.OnDismissPopUp(popUp))
                    }
                )
            }
            is OverviewScreenPopUp.ExamScoreInputDialog -> {
                ScoreDialog(
                    initialScore = popUp.exam.exam.score ?: 0,
                    onMarkNotDoneYet = {
                        viewModel.onUIEvent(OverviewScreenUIEvent.OnMarkExamAsUndone(popUp.exam))
                    },
                    onScoreConfirmed = {
                        viewModel.onUIEvent(OverviewScreenUIEvent.OnExamScorePicked(popUp.exam, it))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(OverviewScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
            is OverviewScreenPopUp.Loading -> {
                LoadingAlertDialog(
                    message = stringResource(R.string.loading)
                )
            }
        }
    }

}


@Composable
private fun OverviewScreen(
    modifier: Modifier = Modifier,
    state: OverviewScreenUIState,
    onUIEvent: (OverviewScreenUIEvent) -> Unit,
) {
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.overview),
                onMenuClick = {
                    onUIEvent(OverviewScreenUIEvent.OnHamburgerMenuClick)
                }
            )
        },
        floatingActionButton = {
            HomeworkExamAndReminderFAB(
                isExpanded = fabExpanded,
                onClick = { fabExpanded = !fabExpanded },
                onDismiss = { fabExpanded = false },
                onReminderFabClick = {
                    onUIEvent(OverviewScreenUIEvent.OnReminderFABClick)
                },
                onExamFabClick = {
                    onUIEvent(OverviewScreenUIEvent.OnExamFABClick)
                },
                onHomeworkFabClick = {
                    onUIEvent(OverviewScreenUIEvent.OnHomeworkFABClick)
                },
            )
        }
    ) {
        OverviewScreenMainContent(
            modifier = modifier.padding(it),
            state = state,
            onUIEvent = onUIEvent,
        )
    }
}


@Suppress("t")
@Composable
private fun OverviewScreenMainContent(
    modifier: Modifier = Modifier,
    state: OverviewScreenUIState,
    onUIEvent: (OverviewScreenUIEvent) -> Unit,
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
                    onUIEvent(OverviewScreenUIEvent.OnEventClick(event))
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
