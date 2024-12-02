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
import androidx.compose.ui.graphics.Color
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
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.ExamCategory
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.ui.component.eventcard.EventCard
import com.wahyusembiring.ui.component.scoredialog.ScoreDialog
import com.wahyusembiring.ui.component.floatingactionbutton.HomeworkExamAndReminderFAB
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.topappbar.TopAppBar
import com.wahyusembiring.ui.component.v2.overviewcard.EmptyEventCard
import com.wahyusembiring.ui.component.v2.overviewcard.ExamCard
import com.wahyusembiring.ui.component.v2.overviewcard.HomeworkCard
import com.wahyusembiring.ui.component.v2.overviewcard.ReminderCard
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale


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
    val currentDate = remember { LocalDate.now() }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        repeat(7) { dayOffset ->
            val events = when (dayOffset) {
                0 -> state.todayEvents
                1 -> state.tomorrowEvents
                2 -> state.day3rdEvents
                3 -> state.day4thEvents
                4 -> state.day5thEvents
                5 -> state.day6thEvents
                6 -> state.day7thEvents
                else -> emptyList()
            }
            item {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
                DaySectionHeader(
                    title = when (dayOffset) {
                        0 -> stringResource(R.string.today)
                        1 -> stringResource(R.string.tomorrow)
                        else -> {
                            currentDate.plusDays(dayOffset.toLong()).format(DateTimeFormatter.ofPattern("EEEE", Locale.getDefault()))
                        }
                    },
                    date = when (dayOffset) {
                        0, 1 -> {
                            currentDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL))
                        }
                        else -> {
                            currentDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
                        }
                    }
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            }
            if (events.isEmpty()) {
                item {
                    EmptyEventCard(
                        modifier = Modifier.padding(
                            vertical = MaterialTheme.spacing.Medium,
                            horizontal = MaterialTheme.spacing.Large
                        )
                    )
                }
            }
            items(
                items = events,
                key = { it.hashCode() }
            ) { event ->
                when (event) {
                    is HomeworkWithSubject -> {
                        HomeworkCard(
                            modifier = Modifier.padding(
                                vertical = MaterialTheme.spacing.Small,
                                horizontal = MaterialTheme.spacing.Large
                            ),
                            homeworkWithSubject = event,
                        )
                    }
                    is ExamWithSubject -> {
                        ExamCard(
                            modifier = Modifier.padding(
                                vertical = MaterialTheme.spacing.Small,
                                horizontal = MaterialTheme.spacing.Large
                            ),
                            examWithSubject = event,
                        )
                    }
                    is Reminder -> {
                        ReminderCard(
                            modifier = Modifier.padding(
                                vertical = MaterialTheme.spacing.Small,
                                horizontal = MaterialTheme.spacing.Large
                            ),
                            reminder = event,
                            dateTime = event.reminderDates.first()
                        )
                    }
                }
            }
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

@Preview
@Composable
private fun OverviewScreenPreview() {

    val dummySubject = Subject(
        name = "Subject 1",
        color = Color.Green,
        lecturerId = "",
        room = "20",
        description = ""
    )

    val dummyReminder = Reminder(
        title = "Reminder 1",
        notes = "Description 1",
        reminderDates = listOf(LocalDateTime.now()),
    )

    val dummyHomework = HomeworkWithSubject(
        homework = Homework(
            title = "Homework 1",
            notes = "Description 1",
            dueDate = LocalDateTime.now(),
            deadline = LocalDateTime.now(),
            dueReminder = null,
            deadlineReminder = null,
            completed = false,
            subjectId = ""
        ),
        subject = dummySubject
    )

    val dummyExam = ExamWithSubject(
        exam = Exam(
            title = "Exam 1",
            notes = "Description 1",
            dueDate = LocalDateTime.now(),
            deadline = LocalDateTime.now(),
            dueReminder = null,
            deadlineReminder = null,
            score = null,
            subjectId = "",
            category = ExamCategory.WRITTEN
        ),
        subject = dummySubject
    )


    HabitTheme {
        OverviewScreen(
            state = OverviewScreenUIState(
                todayEvents = listOf(
                    dummyReminder,
                    dummyHomework,
                    dummyExam,
                    dummyReminder.copy(id = "1")
                )
            ),
            onUIEvent = {}
        )
    }
}