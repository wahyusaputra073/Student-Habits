package com.wahyusembiring.exam

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.ui.ReminderOption
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.success.SuccessAlertDialog
import com.wahyusembiring.ui.component.popup.picker.examcategorypicker.ExamCategoryPicker
import com.wahyusembiring.ui.component.popup.picker.subjectpicker.SubjectPicker
import com.wahyusembiring.ui.component.v2.datetimepicker.DateTimePicker
import com.wahyusembiring.ui.component.v2.datetimepicker.rememberDateTimePickerState
import com.wahyusembiring.ui.component.scoredialog.ScoreDialog
import com.wahyusembiring.ui.theme.spacing
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExamScreen(
    viewModel: ExamScreenViewModel,
    navController: NavHostController,
) {
    val state by viewModel.state.collectAsState()

    CollectAsOneTimeEvent(viewModel.navigationEvent) { event ->
        when (event) {
            CreateExamScreenNavigationEvent.NavigateBack -> {
                navController.navigateUp()
            }
            CreateExamScreenNavigationEvent.NavigateToCreateSubject -> {
                navController.navigate(Screen.CreateSubject())
            }
        }
    }

    ExamScreenUI(
        state = state,
        onUIEvent = viewModel::onUIEvent,
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is CreateExamScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.errorMessage.asString(),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    }
                )
            }
            is CreateExamScreenPopUp.ExamSavedDialog -> {
                SuccessAlertDialog(
                    title = stringResource(R.string.success),
                    message = stringResource(R.string.exam_saved),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnNavigateBackRequest)
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnNavigateBackRequest)
                    },
                )
            }
            is CreateExamScreenPopUp.Loading -> {
                LoadingAlertDialog(message = stringResource(R.string.loading))
            }
            is CreateExamScreenPopUp.SaveConfirmationDialog -> {
                ConfirmationAlertDialog(
                    title = stringResource(R.string.save_exam),
                    message = if (state.isEditMode) {
                        stringResource(R.string.are_you_sure_you_want_to_edit_this_exam)
                    } else {
                        stringResource(R.string.are_you_sure_you_want_to_save_this_exam)
                    },
                    positiveButtonText = stringResource(R.string.save),
                    onPositiveButtonClick = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnSaveExamConfirmClick)
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    negativeButtonText = stringResource(R.string.cancel),
                    onNegativeButtonClick = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
            is CreateExamScreenPopUp.SubjectPicker -> {
                SubjectPicker(
                    subjects = state.subjects,
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onSubjectSelected = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnSubjectPicked(it))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    navigateToCreateSubjectScreen = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnNavigateToSubjectScreenRequest)
                    }
                )
            }
            CreateExamScreenPopUp.CustomExamCategoryPicker -> {
                ExamCategoryPicker(
                    initialCategory = state.category,
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onCategoryPicked = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnCategoryPicked(it))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    }
                )
            }

            CreateExamScreenPopUp.CustomDeadlineReminderPicker -> {
                val dateTimePicker = rememberDateTimePickerState(initialDateTime = state.deadline)
                DateTimePicker(
                    state = dateTimePicker,
                    minDate = state.dueDate.toLocalDate(),
                    maxDate = state.deadline.toLocalDate(),
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDateTimeSelected = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnExamDeadlineReminderChanged(ReminderOption.Custom(it)))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    allowedDateTime = {
                        it.isAfter(state.dueDate) && it.isBefore(state.deadline)
                    }
                )
            }
            CreateExamScreenPopUp.DuePeriodPicker -> {

            }
            CreateExamScreenPopUp.CustomDueReminderPicker -> {
                val dateTimePicker = rememberDateTimePickerState(initialDateTime = state.deadline)
                DateTimePicker(
                    state = dateTimePicker,
                    minDate = LocalDate.now(),
                    maxDate = state.dueDate.toLocalDate(),
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDateTimeSelected = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnExamDayReminderChanged(ReminderOption.Custom(it)))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    allowedDateTime = {
                        it.isAfter(state.dueDate) && it.isBefore(state.deadline)
                    }
                )
            }
            CreateExamScreenPopUp.ScoreInputDialog -> {
                ScoreDialog(
                    initialScore = state.score ?: 0,
                    onDismissRequest = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onMarkNotDoneYet = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnExamScoreChanged(null))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onScoreConfirmed = {
                        viewModel.onUIEvent(ExamScreenUIEvent.OnExamScoreChanged(it))
                        viewModel.onUIEvent(ExamScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExamScreenUI(
    state: ExamScreenUIState,
    onUIEvent: (ExamScreenUIEvent) -> Unit,
) {

    val mainContainerScrollState = rememberScrollState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val localDensity = LocalDensity.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onUIEvent(ExamScreenUIEvent.OnNavigateBackRequest)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            onUIEvent(ExamScreenUIEvent.OnSaveExamButtonClick)
                        }
                    ) {
                        Text(
                            text = if (state.isEditMode) {
                                stringResource(R.string.edit)
                            } else {
                                stringResource(R.string.save)
                            }
                        )
                    }
                }
            )
        }
    ) { scaffoldPadding ->

        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize()
                .verticalScroll(mainContainerScrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            0.2f to Color.Transparent,
                            0.6f to MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            1f to MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                        )
                    )
            ) {
                HorizontalDivider(
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.Small)
                        .align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.surface
                )
                Column(
                    modifier = Modifier.padding(MaterialTheme.spacing.Large)
                ) {
                    Text(
                        text = if (state.isEditMode) {
                            stringResource(R.string.edit)
                        } else {
                            stringResource(R.string.create)
                        },
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = stringResource(R.string.exam),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(bottom = MaterialTheme.spacing.Small)
                        .align(Alignment.BottomCenter),
                    color = MaterialTheme.colorScheme.surface
                )
            }
            Column(
                modifier = Modifier.padding(MaterialTheme.spacing.Large)
            ) {
                Column {

                    if (state.isEditMode) {

                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                    }
                    Text(
                        text = stringResource(R.string.exam_name),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small),
                        value = state.examTitle,
                        textStyle = if (state.score != null) {
                            LocalTextStyle.current.copy(
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        } else LocalTextStyle.current,
                        leadingIcon = if (state.score != null) {
                            {
                                AnimatedVisibility(
                                    visible = state.score != null,
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = Color.Green
                                    )
                                }
                            }
                        } else null,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_title),
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = null
                            )
                        },
                        onValueChange = {
                            onUIEvent(ExamScreenUIEvent.OnExamNameChanged(it))
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.please_enter_exam_name),
                                style = MaterialTheme.typography.bodyLarge,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {
                    Text(
                        text = stringResource(R.string.exam_period),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small)
                            .pointerInput(state.dueDate, state.deadline) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)
                                    val upEvent =
                                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                    if (upEvent != null) {
                                        onUIEvent(ExamScreenUIEvent.OnPickExamPeriodButtonClicked)
                                    }
                                }
                            },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.please_select_exam_period),
                                style = MaterialTheme.typography.bodyLarge,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        value = buildString {
                            append(state.dueDate.format(
                                DateTimeFormatter.ofLocalizedDate(
                                    FormatStyle.MEDIUM).withLocale(Locale.getDefault())))
                            append(" - ")
                            append(state.deadline.format(
                                DateTimeFormatter.ofLocalizedDate(
                                    FormatStyle.MEDIUM).withLocale(Locale.getDefault())))
                        },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_calendar),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Unspecified
                        ),
                        singleLine = true,
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {
                    Text(
                        text = stringResource(R.string.subject),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small)
                            .pointerInput(state.subject) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)
                                    val upEvent =
                                        waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                    if (upEvent != null) {
                                        onUIEvent(ExamScreenUIEvent.OnExamSubjectPickerClick)
                                    }
                                }
                            },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.please_select_a_subject),
                                style = MaterialTheme.typography.bodyLarge,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        value = state.subject?.name ?: "",
                        readOnly = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_dropdown),
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            Icon(
                                painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_subjects),
                                contentDescription = null,
                                tint = state.subject?.color ?: MaterialTheme.colorScheme.primary
                            )
                        },
                        onValueChange = {},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Unspecified
                        ),
                        singleLine = true,
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {

                    var showDropdown by remember { mutableStateOf(false) }
                    var dueReminderContainerWidth by remember { mutableStateOf(Dp.Unspecified) }
                    var visible by remember { mutableStateOf(true) }

                    Row(
                        modifier = Modifier
                            .clickable(
                                role = Role.Checkbox,
                                onClick = {
                                    visible = !visible
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = visible,
                            onCheckedChange = null
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = MaterialTheme.spacing.Small),
                            text = stringResource(R.string.notify_me_before_exam_day),
                            color = if (visible) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    AnimatedVisibility(
                        visible = visible,
                    ) {
                        Column(
                            modifier = Modifier
                                .onSizeChanged {
                                    with(localDensity) {
                                        dueReminderContainerWidth = (it.width / density).toInt().dp
                                    }
                                }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = MaterialTheme.spacing.Small)
                                    .pointerInput(state.subject) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent =
                                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                showDropdown = true
                                            }
                                        }
                                    },
                                value = when (val reminder = state.dueReminder) {
                                    is ReminderOption.Predefined -> reminder.displayName.asString()
                                    is ReminderOption.Custom -> reminder.dateTime.format(
                                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(
                                            Locale.getDefault())
                                    )
                                    null -> ""
                                },
                                readOnly = true,
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_dropdown),
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_reminder),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onValueChange = {},
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Unspecified
                                ),
                                singleLine = true,
                                maxLines = 1,
                            )
                            DropdownMenu(
                                modifier = Modifier
                                    .width(dueReminderContainerWidth),
                                expanded = showDropdown,
                                onDismissRequest = { showDropdown = false }
                            ) {
                                for (option in ReminderOption.dueReminderDefaultOptions) {
                                    DropdownMenuItem(
                                        contentPadding = PaddingValues(
                                            horizontal = MaterialTheme.spacing.Large,
                                            vertical = 0.dp
                                        ),
                                        text = { Text(text = option.displayName.asString()) },
                                        onClick = {
                                            onUIEvent(ExamScreenUIEvent.OnExamDayReminderChanged(option))
                                            showDropdown = false
                                        }
                                    )
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = MaterialTheme.spacing.Large,
                                        vertical = 0.dp
                                    ),
                                    text = { Text(text = stringResource(R.string.custom_triple_dot)) },
                                    onClick = {
                                        onUIEvent(ExamScreenUIEvent.OnCustomExamDayReminderButtonClicked)
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {

                    var showDropdown by remember { mutableStateOf(false) }
                    var deadlineReminderContainerWidth by remember { mutableStateOf(Dp.Unspecified) }
                    var visible by remember { mutableStateOf(true) }

                    Row(
                        modifier = Modifier
                            .clickable(
                                role = Role.Checkbox,
                                onClick = {
                                    visible = !visible
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = visible,
                            onCheckedChange = null
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = MaterialTheme.spacing.Small),
                            text = stringResource(R.string.notify_me_before_the_deadline),
                            color = if (visible) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            },
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    AnimatedVisibility(
                        visible = visible,
                    ) {
                        Column(
                            modifier = Modifier
                                .onSizeChanged {
                                    with(localDensity) {
                                        deadlineReminderContainerWidth = (it.width / density).toInt().dp
                                    }
                                }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = MaterialTheme.spacing.Small)
                                    .pointerInput(state.subject) {
                                        awaitEachGesture {
                                            awaitFirstDown(pass = PointerEventPass.Initial)
                                            val upEvent =
                                                waitForUpOrCancellation(pass = PointerEventPass.Initial)
                                            if (upEvent != null) {
                                                showDropdown = true
                                            }
                                        }
                                    },
                                value = when (val reminder = state.deadlineReminder) {
                                    is ReminderOption.Predefined -> reminder.displayName.asString()
                                    is ReminderOption.Custom -> reminder.dateTime.format(
                                        DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT).withLocale(
                                            Locale.getDefault())
                                    )
                                    null -> ""
                                },
                                readOnly = true,
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_dropdown),
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(com.wahyusembiring.ui.R.drawable.ic_reminder),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                onValueChange = {},
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Unspecified
                                ),
                                singleLine = true,
                                maxLines = 1,
                            )
                            DropdownMenu(
                                modifier = Modifier
                                    .width(deadlineReminderContainerWidth),
                                expanded = showDropdown,
                                onDismissRequest = { showDropdown = false }
                            ) {
                                for (option in ReminderOption.deadlineReminderDefaultOptions) {
                                    DropdownMenuItem(
                                        contentPadding = PaddingValues(
                                            horizontal = MaterialTheme.spacing.Large,
                                            vertical = 0.dp
                                        ),
                                        text = { Text(text = option.displayName.asString()) },
                                        onClick = {
                                            onUIEvent(ExamScreenUIEvent.OnExamDeadlineReminderChanged(option))
                                            showDropdown = false
                                        }
                                    )
                                    HorizontalDivider()
                                }
                                DropdownMenuItem(
                                    contentPadding = PaddingValues(
                                        horizontal = MaterialTheme.spacing.Large,
                                        vertical = 0.dp
                                    ),
                                    text = { Text(text = stringResource(R.string.custom_triple_dot)) },
                                    onClick = {
                                        onUIEvent(ExamScreenUIEvent.OnCustomExamDeadlineReminderButtonClicked)
                                        showDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
                Column {
                    Text(
                        text = stringResource(R.string.notes),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.Small))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Small),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.add_some_notes),
                                style = MaterialTheme.typography.bodyMedium,
                                color = OutlinedTextFieldDefaults.colors().disabledTextColor
                            )
                        },
                        value = state.notes,
                        onValueChange = {
                            onUIEvent(ExamScreenUIEvent.OnExamNotesChanged(it))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        minLines = 4,
                        maxLines = 4
                    )
                }
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
            }
        }
    }
}