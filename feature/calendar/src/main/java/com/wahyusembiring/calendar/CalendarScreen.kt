package com.wahyusembiring.calendar

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.DrawerState
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.wahyusembiring.calendar.util.getEventsByDate
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.data.model.ExamWithSubject
import com.wahyusembiring.data.model.HomeworkWithSubject
import com.wahyusembiring.data.model.entity.Exam
import com.wahyusembiring.data.model.entity.Homework
import com.wahyusembiring.data.model.entity.Reminder
import com.wahyusembiring.data.model.entity.Subject
import com.wahyusembiring.ui.component.eventcard.EventCard
import com.wahyusembiring.ui.component.topappbar.TopAppBar
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.adjustHSL
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.day.DefaultDay
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle.FULL
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: CalendarScreenViewModel,
    navController: NavHostController,
    drawerState: DrawerState
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    CollectAsOneTimeEvent(viewModel.navigationEvent) {
        when (it) {
            is CalendarScreenNavigationEvent.NavigateToHomeworkDetail -> {
                navController.navigate(Screen.CreateHomework(it.homeworkId))
            }
            is CalendarScreenNavigationEvent.NavigateToExamDetail -> {
                navController.navigate(Screen.CreateExam(it.examkId))
            }
            is CalendarScreenNavigationEvent.NavigateToReminderDetail -> {
                navController.navigate(Screen.CreateReminder(it.reminderId))
            }

            else -> {}
        }
    }

    CalendarScreen(
        state = state.value,
        onUIEvent = viewModel::onUIEvent,
        onHamburgerMenuClick = {
            coroutineScope.launch { drawerState.open() }
        }
    )

}

@Composable
private fun CalendarScreen(
    state: CalendarScreenUIState,
    onUIEvent: (CalendarScreenUIEvent) -> Unit,
    onHamburgerMenuClick: () -> Unit,
) {
    val context = LocalContext.current
    var selectedDate: LocalDate? by remember { mutableStateOf(LocalDate.now()) }
    val calendarState = rememberSelectableCalendarState(
        initialSelection = listOf(LocalDate.now()),
        confirmSelectionChange = {
            selectedDate = it.firstOrNull()
            true
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.calendar),
                onMenuClick = onHamburgerMenuClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SelectableCalendar(
                modifier = Modifier.padding(
                    horizontal = MaterialTheme.spacing.Medium,
                    vertical = MaterialTheme.spacing.Small
                ),
                calendarState = calendarState,
                monthHeader = {
                    MonthHeader(monthState = it)
                },
                dayContent = {
                    DefaultDay(state = it)
                    EventIndicator(dayState = it, events = state.events)
                }
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
            selectedDate?.let { date ->
                EventCard(
                    modifier = Modifier.padding(
                        horizontal = MaterialTheme.spacing.Large,
                        vertical = MaterialTheme.spacing.Small
                    ),
                    events = state.events.getEventsByDate(date),
                    onEventClick = {
                        onUIEvent(CalendarScreenUIEvent.OnEventClick(it))
                    },
                    onDeletedEventClick = { event ->
                        onUIEvent(CalendarScreenUIEvent.OnDeleteEvent(event))
                    },
                    onEventCheckedChange = { event, isChecked ->
                        onUIEvent(CalendarScreenUIEvent.OnEventCompletedStateChange(event, isChecked))
                    },
                )
            }
        }
    }
}

@Composable
private fun MonthHeader(
    monthState: MonthState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DecrementButton(monthState = monthState)
        Text(
            text = monthState.currentMonth.month
                .getDisplayName(FULL, Locale.getDefault())
                .lowercase()
                .replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = monthState.currentMonth.year.toString(),
            style = MaterialTheme.typography.titleMedium
        )
        IncrementButton(monthState = monthState)
    }
}

@Composable
private fun DecrementButton(
    monthState: MonthState,
) {
    IconButton(
        enabled = monthState.currentMonth > monthState.minMonth,
        onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }
    ) {
        Image(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            contentDescription = "Previous",
        )
    }
}

@Composable
private fun IncrementButton(
    monthState: MonthState,
) {
    IconButton(
        enabled = monthState.currentMonth < monthState.maxMonth,
        onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }
    ) {
        Image(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            contentDescription = "Next",
        )
    }
}

@Composable
private fun BoxScope.EventIndicator(
    dayState: DayState<DynamicSelectionState>,
    events: List<Any>
) {
    val eventInThisDay = events.getEventsByDate(dayState.date)
    val isThereAnyExam = eventInThisDay.any { it is ExamWithSubject }
    val isThereAnyHomework = eventInThisDay.any { it is HomeworkWithSubject }
    val isThereAnyReminder = eventInThisDay.any { it is Reminder }

    Row(
        modifier = Modifier
            .padding(bottom = 6.dp)
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        if (isThereAnyHomework) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50)
                    )
            )
        }
        if (isThereAnyExam) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.adjustHSL(hue = 120f),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
        if (isThereAnyReminder) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.adjustHSL(hue = 180f),
                        shape = RoundedCornerShape(50)
                    )
            )
        }
    }
}
