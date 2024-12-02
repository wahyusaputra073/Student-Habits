package com.wahyusembiring.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.onboarding.component.PageIndicator
import com.wahyusembiring.onboarding.model.OnBoardingModel
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.theme.spacing
import kotlinx.coroutines.launch

@Composable
fun OnBoardingScreen(
    viewModel: OnBoardingScreenViewModel,
    navController: NavHostController,
) {

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val navigationEvent = viewModel.navigationEvent
    CollectAsOneTimeEvent(navigationEvent) {
        when (it) {
            is OnBoardingScreenNavigationEvent.NavigateToLogin -> {
                navController.navigate(Screen.Login) {
                    popUpTo(Screen.Login) {
                        inclusive = true
                    }
                }
            }
        }
    }

    OnBoardingScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is OnBoardingScreenPopUp.Loading -> {
                LoadingAlertDialog(stringResource(R.string.loading))
            }
            is OnBoardingScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.message.asString(),
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onUIEvent(OnBoardingScreenUIEvent.OnDismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onUIEvent(OnBoardingScreenUIEvent.OnDismissPopUp(popUp))
                    }
                )
            }
        }
    }

}

@Suppress("t")
@Composable
private fun OnBoardingScreen(
    state: OnBoardingScreenUIState,
    onUIEvent: (OnBoardingScreenUIEvent) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = 0) {
        state.models.size
    }
    val coroutineScope = rememberCoroutineScope()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState
            ) {
                OnBoardingScreen(model = state.models[it])
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing.Medium,
                        vertical = MaterialTheme.spacing.Small
                    ),
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.back))
                    }
                }
                PageIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    pageSize = state.models.size,
                    currentPage = pagerState.currentPage
                )
                Button(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = {
                        coroutineScope.launch {
                            if (pagerState.currentPage == state.models.size - 1) {
                                onUIEvent(OnBoardingScreenUIEvent.OnCompleted)
                            } else {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                ) {
                    Text(
                        text = if (pagerState.currentPage == state.models.size - 1) {
                            stringResource(R.string.get_started)
                        } else {
                            stringResource(R.string.next)
                        }
                    )
                }
            }
        }
    }

}

@Composable
private fun OnBoardingScreen(
    model: OnBoardingModel,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = model.image,
            contentDescription = model.title,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp),
            alignment = Alignment.Center
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))

        Text(
            text = model.title,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))

        Text(
            text = model.description,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

    }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun OnBoardingPermissionScreen() {

    val context = LocalContext.current

    val activityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {

    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = R.drawable.onboarding_reminder_permission,
            contentDescription = stringResource(R.string.permission_needed),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 64.dp),
            alignment = Alignment.Center
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Large))

        Text(
            text = stringResource(R.string.permission_needed),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))

        Text(
            text = stringResource(R.string.to_be_able_to_use_the_application_properly_please_give_permission_to_schedule_reminders),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(MaterialTheme.spacing.Medium))

        Button(
            onClick = {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                intent.setData(Uri.fromParts("package", context.packageName, null))
                activityLauncher.launch(intent)
            }
        ) {
            Text(text = stringResource(R.string.grant_permission))
        }

    }
}