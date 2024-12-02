package com.wahyusembiring.thesisplanner.screen.planner

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.wahyusembiring.data.model.entity.Task
import com.wahyusembiring.data.util.toFile
import com.wahyusembiring.thesisplanner.R
import com.wahyusembiring.thesisplanner.component.ArticleList
import com.wahyusembiring.thesisplanner.component.Section
import com.wahyusembiring.thesisplanner.component.TaskList
import com.wahyusembiring.ui.component.button.DatePickerButton
import com.wahyusembiring.ui.component.modalbottomsheet.component.NavigationAndActionButtonHeader
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.picker.datepicker.DatePicker
import com.wahyusembiring.ui.theme.spacing
import com.wahyusembiring.ui.util.checkForPermissionOrLaunchPermissionLauncher
import com.wahyusembiring.ui.util.getFileAccessPermissionRequest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

@Composable
fun ThesisPlannerScreen(
    viewModel: ThesisPlannerScreenViewModel,
    drawerState: DrawerState,
    navController: NavHostController
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()


    ThesisPlannerScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent,
        onNavigateBack = {
            navController.navigateUp()
        }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThesisPlannerScreen(
    state: ThesisPlannerScreenUIState,
    onUIEvent: (ThesisPlannerScreenUIEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val context = LocalContext.current

    val documentPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
            coroutineScope.launch {
                uris.forEach {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
                val files = uris.map { it.toFile(context) }
                onUIEvent(ThesisPlannerScreenUIEvent.OnDocumentPickerResult(files))
            }
        }

    val documentPermissionRequestLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            documentPickerLauncher.launch(arrayOf("application/pdf"))
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.thesis_planner))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back_to_thesis_selection_screen)
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
                .scrollable(
                    state = scrollState,
                    orientation = Orientation.Vertical
                )
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.Medium,
                        vertical = MaterialTheme.spacing.Large
                    ),
                label = { Text(text = stringResource(R.string.thesis_title)) },
                value = state.editedThesisTitle,
                singleLine = true,
                onValueChange = { onUIEvent(ThesisPlannerScreenUIEvent.OnThesisTitleChange(it)) }
            )
            HorizontalDivider()
            Section(
                title = stringResource(R.string.articles),
                trailingContent = {
                    IconButton(
                        onClick = {
                            checkForPermissionOrLaunchPermissionLauncher(
                                context = context,
                                permissionToRequest = getFileAccessPermissionRequest(),
                                permissionRequestLauncher = documentPermissionRequestLauncher,
                                onPermissionAlreadyGranted = {
                                    documentPickerLauncher.launch(
                                        arrayOf(
                                            "application/pdf"
                                        )
                                    )
                                }
                            )
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_article)
                        )
                    }
                }
            ) {
                ArticleList(
                    articles = state.articles,
                    onArticleClick = { onUIEvent(ThesisPlannerScreenUIEvent.OnArticleClick(it)) },
                    onDeleteArticleClick = {
                        onUIEvent(
                            ThesisPlannerScreenUIEvent.OnDeleteArticleClick(it)
                        )
                    }
                )
            }
            HorizontalDivider()
            Section(
                title = stringResource(R.string.task),
                trailingContent = {
                    IconButton(
                        onClick = {
                            onUIEvent(ThesisPlannerScreenUIEvent.OnCreateTaskButtonClick)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_task)
                        )
                    }
                }
            ) {
                TaskList(
                    tasks = state.tasks,
                    onCompletedStatusChange = { task, isCompleted ->
                        onUIEvent(
                            ThesisPlannerScreenUIEvent
                                .OnTaskCompletedStatusChange(task, isCompleted)
                        )
                    },
                    onDeleteTaskClick = { onUIEvent(ThesisPlannerScreenUIEvent.OnDeleteTaskClick(it)) }
                )
            }
            HorizontalDivider()
        }
    }
    if (state.showCreateTaskDialog) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                coroutineScope
                    .launch { sheetState.hide() }
                    .invokeOnCompletion {
                        onUIEvent(ThesisPlannerScreenUIEvent.OnCreateTaskDialogDismiss)
                    }
            }
        ) {
            var task by remember {
                mutableStateOf(
                    Task(
                        thesisId = UUID.randomUUID().toString(),
                        name = "",
                        dueDate = LocalDate.now()
                    )
                )
            }

            NavigationAndActionButtonHeader(
                onNavigationButtonClicked = {
                    coroutineScope
                        .launch { sheetState.hide() }
                        .invokeOnCompletion {
                            onUIEvent(ThesisPlannerScreenUIEvent.OnCreateTaskDialogDismiss)
                        }
                },
                onActionButtonClicked = {
                    onUIEvent(ThesisPlannerScreenUIEvent.OnSaveTaskClick(task))
                    coroutineScope
                        .launch { sheetState.hide() }
                        .invokeOnCompletion {
                            onUIEvent(ThesisPlannerScreenUIEvent.OnCreateTaskDialogDismiss)
                        }
                },
                navigationButtonDescription = "Cancel create task"
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.Medium,
                        vertical = MaterialTheme.spacing.Large
                    ),
                label = { Text(text = stringResource(R.string.task_name)) },
                singleLine = true,
                value = task.name,
                onValueChange = { task = task.copy(name = it) },
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
            Text(
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.Medium),
                text = stringResource(R.string.due_date),
                style = MaterialTheme.typography.bodySmall,
            )
            DatePickerButton(
                date = task.dueDate,
                onClicked = {
                    onUIEvent(ThesisPlannerScreenUIEvent.OnDatePickerButtonClick)
                }
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))
            if (state.showDatePicker) {
                DatePicker(
                    onDismissRequest = {
                        onUIEvent(ThesisPlannerScreenUIEvent.OnDatePickerDismiss)
                    },
                    onDateSelected = {
                        task = task.copy(dueDate = it)
                    }
                )
            }
        }
    }

    if (state.articlePendingDelete != null) {
        ConfirmationAlertDialog(
            title = stringResource(R.string.delete_article),
            message = stringResource(R.string.are_you_sure_you_want_to_delete_this_thesis),
            positiveButtonText = stringResource(R.string.yes),
            onPositiveButtonClick = {
                onUIEvent(
                    ThesisPlannerScreenUIEvent.OnDeleteArticleConfirm(state.articlePendingDelete)
                )
                onUIEvent(ThesisPlannerScreenUIEvent.OnArticleDeleteDialogDismiss)
            },
            negativeButtonText = stringResource(R.string.no),
            onNegativeButtonClick = {
                onUIEvent(ThesisPlannerScreenUIEvent.OnArticleDeleteDialogDismiss)
            },
            onDismissRequest = {
                onUIEvent(ThesisPlannerScreenUIEvent.OnArticleDeleteDialogDismiss)
            },
        )
    }

    if (state.taskPendingDelete != null) {
        ConfirmationAlertDialog(
            title = stringResource(R.string.delete_task),
            message = stringResource(R.string.are_you_sure_you_want_to_delete_this_task),
            positiveButtonText = stringResource(R.string.yes),
            onPositiveButtonClick = {
                onUIEvent(
                    ThesisPlannerScreenUIEvent.OnTaskDeleteConfirm(state.taskPendingDelete)
                )
                onUIEvent(ThesisPlannerScreenUIEvent.OnTaskDeleteDialogDismiss)
            },
            negativeButtonText = stringResource(R.string.no),
            onNegativeButtonClick = {
                onUIEvent(ThesisPlannerScreenUIEvent.OnTaskDeleteDialogDismiss)
            },
            onDismissRequest = {
                onUIEvent(ThesisPlannerScreenUIEvent.OnTaskDeleteDialogDismiss)
            },
        )
    }

}