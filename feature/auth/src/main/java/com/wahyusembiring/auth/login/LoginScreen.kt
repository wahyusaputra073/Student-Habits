package com.wahyusembiring.auth.login

import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.wahyusembiring.auth.R
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.theme.HabitTheme
import com.wahyusembiring.ui.theme.spacing

@Composable
fun LoginScreen(
    viewModel: LoginScreenViewModel,
    navController: NavController
) {

    val state by viewModel.state.collectAsState()

    CollectAsOneTimeEvent(viewModel.navigationEvent) {
        when (it) {
            is LoginScreenNavigationEvent.NavigateToHomeScreen -> {
                navController.navigate(Screen.Overview) {
                    popUpTo(navController.graph.id)
                }
            }

            LoginScreenNavigationEvent.NavigateToRegisterScreen -> {
                navController.navigate(Screen.Register) {
                    launchSingleTop = true
                }
            }

//            LoginScreenNavigationEvent.NavigateToHomeScreen -> TODO()
//            LoginScreenNavigationEvent.NavigateToRegisterScreen -> TODO()
        }
    }

    LoginScreen(
        state = state,
        onUIEvent = viewModel::onUIEvent,
    )

    for (popUp in state.popUpStack) {
        when (popUp) {
            is LoginScreenPopUp.SignInLoading -> {
                LoadingAlertDialog(message = stringResource(R.string.logging_in))
            }

            is LoginScreenPopUp.SignInFailed -> {
                ErrorAlertDialog(
                    message = stringResource(R.string.login_failed),
                    buttonText = stringResource(R.string.ok),
                    onDismissRequest = {
                        viewModel.onUIEvent(LoginScreenUIEvent.OnPopupDismissRequest(popUp))
                    },
                    onButtonClicked = {
                        viewModel.onUIEvent(LoginScreenUIEvent.OnPopupDismissRequest(popUp))
                    },
                )
            }

            LoginScreenPopUp.CommonLoading -> {
                LoadingAlertDialog(message = "")
            }

            LoginScreenPopUp.CommonLoading -> TODO()
            LoginScreenPopUp.SignInFailed -> TODO()
            LoginScreenPopUp.SignInLoading -> TODO()
        }
    }

}

@Composable
fun LoginScreen(
    state: LoginScreenUIState,
    onUIEvent: (LoginScreenUIEvent) -> Unit,
) {

    val context = LocalContext.current
    val activityResultRegistryOwner = LocalActivityResultRegistryOwner.current

    Scaffold { scaffoldPadding ->
        Box(
            modifier = Modifier
                .padding(scaffoldPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(MaterialTheme.spacing.Large)
                    .zIndex(2f),
                onClick = {
                    onUIEvent(LoginScreenUIEvent.OnLoginSkipButtonClick)
                }
            ) {
                Text(
                    text = "Skip"
                )
            }
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(MaterialTheme.spacing.Medium, MaterialTheme.spacing.Large)
                    .zIndex(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.sign_in),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.Medium, MaterialTheme.spacing.Large),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = stringResource(R.string.sign_in_with_an_existing_account_or_skip_to_signing_in_as_a_guest),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Medium),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.Medium),
                        value = state.email,
                        onValueChange = {
                            onUIEvent(LoginScreenUIEvent.OnEmailChange(it))
                        },
                        label = { Text(text = stringResource(R.string.email)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        maxLines = 1,
                        singleLine = true
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Medium),
                        value = state.password,
                        onValueChange = {
                            onUIEvent(LoginScreenUIEvent.OnPasswordChange(it))
                        },
                        label = { Text(text = stringResource(R.string.password)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        maxLines = 1,
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                    )
                    Button(
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.Large),
                        onClick = {
                            onUIEvent(LoginScreenUIEvent.OnLoginButtonClick)
                        },
                    ) {
                        Text(text = stringResource(R.string.sign_in))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        HorizontalDivider(modifier = Modifier.width(70.dp))
                        Text(
                            modifier = Modifier.padding(horizontal = MaterialTheme.spacing.Medium),
                            text = stringResource(R.string.or_sign_in_with),
                            style = MaterialTheme.typography.bodySmall
                        )
                        HorizontalDivider(modifier = Modifier.width(70.dp))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.spacing.Large),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    onUIEvent(
                                        LoginScreenUIEvent.OnLoginWithGoogleButtonClick(
                                            context
                                        )
                                    )
                                },
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = stringResource(R.string.google)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
                        Image(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    onUIEvent(
                                        LoginScreenUIEvent.OnLoginWithFacebookButtonClick(
                                            activityResultRegistryOwner
                                        )
                                    )
                                },
                            painter = painterResource(id = R.drawable.facebook),
                            contentDescription = stringResource(R.string.facebook)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.don_t_have_an_account_yet),
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(
                            onClick = {
                                onUIEvent(LoginScreenUIEvent.OnRegisterHereButtonClick)
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.sign_up_here),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    HabitTheme {
        LoginScreen(
            state = LoginScreenUIState(),
            onUIEvent = {}
        )
    }
}