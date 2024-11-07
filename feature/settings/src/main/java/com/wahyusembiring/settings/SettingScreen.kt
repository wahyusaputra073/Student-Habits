package com.wahyusembiring.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.ui.component.popup.alertdialog.confirmation.ConfirmationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun SettingScreen(
    viewModel: SettingScreenViewModel,
    navController: NavController,
    drawerState: DrawerState
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    CollectAsOneTimeEvent(viewModel.navigationEvent) {
        when (it) {
            is SettingScreenNavigationEvent.NavigateToLogin -> {
                navController.navigate(Screen.Login) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    CollectAsOneTimeEvent(viewModel.uiOneTimeEvent) {
        when (it) {
            is SettingScreenEvent.HamburgerMenuButtonClicked -> {
                coroutineScope.launch { drawerState.open() }
            }
            else -> Unit
        }
    }

    SettingScreen(
        state = state,
        onEvent = viewModel::onEvent
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is SettingScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.errorMessages,
                    buttonText = stringResource(R.string.ok),
                    onDismissRequest = {
                        viewModel.onEvent(SettingScreenEvent.DismissPopUp(popUp))
                    },
                    onButtonClicked = {
                        viewModel.onEvent(SettingScreenEvent.DismissPopUp(popUp))
                    },
                )
            }
            is SettingScreenPopUp.Loading -> {
                LoadingAlertDialog(
                    message = stringResource(R.string.loading)
                )
            }
            is SettingScreenPopUp.LogoutConfirmation -> {
                ConfirmationAlertDialog(
                    title = stringResource(R.string.logout),
                    message = stringResource(R.string.are_you_sure_you_want_to_logout),
                    positiveButtonText = stringResource(R.string.yes),
                    onPositiveButtonClick = {
                        viewModel.onEvent(SettingScreenEvent.LogoutConfirmed)
                    },
                    negativeButtonText = stringResource(R.string.no),
                    onNegativeButtonClick = {
                        viewModel.onEvent(SettingScreenEvent.DismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onEvent(SettingScreenEvent.DismissPopUp(popUp))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingScreen(
    state: SettingScreenState,
    onEvent: (SettingScreenEvent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onEvent(SettingScreenEvent.HamburgerMenuButtonClicked)
                        }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_hamburger_menu),
                            contentDescription = null
                        )
                    }
                },
            )
        }
    ) {  scaffoldPadding ->
        Column(
            modifier = Modifier.padding(scaffoldPadding)
        ) {
            ListItem(
                modifier = Modifier
                    .clickable {
                        onEvent(SettingScreenEvent.LogoutButtonClicked)
                    },
                leadingContent = {
                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = null
                    )
                },
                headlineContent = {
                    Text(text = stringResource(R.string.logout))
                },
            )
            HorizontalDivider(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.spacing.Large)
            )
        }
    }
}