package com.wahyusembiring.subject.screen.main

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.imageLoader
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.subject.R
import com.wahyusembiring.ui.component.modalbottomsheet.component.AddNewSubject
import com.wahyusembiring.ui.component.modalbottomsheet.component.SubjectListItem
import com.wahyusembiring.ui.component.modalbottomsheet.component.SubjectListItemMenu
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.component.topappbar.TopAppBar
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun SubjectScreen(
    viewModel: SubjectScreenViewModel,
    navController: NavController,
    drawerState: DrawerState
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    CollectAsOneTimeEvent(viewModel.navigationEvent) {
        when (it) {
            is SubjectScreenNavigationEvent.NavigateToSubjectDetail -> {
                navController.navigate(Screen.CreateSubject(it.subject.id))
            }

            is SubjectScreenNavigationEvent.NavigateToCreateSubject -> {
                navController.navigate(Screen.CreateSubject())
            }
        }
    }

    CollectAsOneTimeEvent(viewModel.oneTimeEvent) {  event ->
        when (event) {
            is SubjectScreenUIEvent.OnHamburgerMenuClick -> {
                drawerState.open()
            }
            else -> Unit
        }
    }

    SubjectScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is SubjectScreenPopUp.Loading -> {
                LoadingAlertDialog(stringResource(R.string.loading))
            }

            is SubjectScreenPopUp.DeleteSubjectConfirmation -> {
                ConfirmationAlertDialog(
                    title = stringResource(R.string.delete_subject),
                    message = stringResource(R.string.are_you_sure_you_want_to_delete_this_subject),
                    positiveButtonText = stringResource(R.string.delete),
                    onPositiveButtonClick = {
                        viewModel.onUIEvent(SubjectScreenUIEvent.OnDismissPopUp(popUp))
                        viewModel.onUIEvent(SubjectScreenUIEvent.OnSubjectDeleteConfirmed(popUp.subject))
                    },
                    negativeButtonText = stringResource(R.string.cancel),
                    onNegativeButtonClick = {
                        viewModel.onUIEvent(SubjectScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(SubjectScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
            is SubjectScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.errorMessage.asString(),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(SubjectScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(SubjectScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
            is SubjectScreenPopUp.SubjectDeleted -> {
                InformationAlertDialog(
                    title = stringResource(R.string.success),
                    message = stringResource(R.string.subject_deleted_successfully),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(SubjectScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(SubjectScreenUIEvent.OnDismissPopUp(popUp))
                    },
                )
            }
        }
    }

}

@Composable
@Suppress("t")
private fun SubjectScreen(
    state: SubjectScreenUIState,
    onUIEvent: (event: SubjectScreenUIEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.subject),
                onMenuClick = { onUIEvent(SubjectScreenUIEvent.OnHamburgerMenuClick) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onUIEvent(SubjectScreenUIEvent.OnFloatingActionButtonClick) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null
                )
            }
        }
    ) { scaffoldPadding ->
        Column(
            modifier = Modifier
                .padding(scaffoldPadding)
                .scrollable(scrollState, Orientation.Vertical)
        ) {
            state.subjects.forEach { subject ->
                SubjectListItemMenu(
                    subject = subject.subject,
                    onClicked = { selectedSubject ->
                        onUIEvent(SubjectScreenUIEvent.OnSubjectClick(selectedSubject))
                    },
                    onDeleteSubClick = { onUIEvent(SubjectScreenUIEvent.OnDeleteSubjectClick(subject.subject)) }
                )
            }

            if (state.subjects.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AsyncImage(
                            modifier = Modifier.width(64.dp),
                            model = R.drawable.no_data_picture,
                            contentDescription = null,
                            imageLoader = context.imageLoader
                        )
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))
                        Text(
                            text = stringResource(id = R.string.you_don_t_have_any_subject),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

//            AddNewSubject(
//                onClicked = {
//                    onUIEvent(SubjectScreenUIEvent.OnFloatingActionButtonClick)
//                }
//            )
        }
    }
}


