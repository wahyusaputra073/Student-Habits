package com.wahyusembiring.auth.register

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.wahyusembiring.auth.EmailValidationError
import com.wahyusembiring.auth.MIN_PASSWORD_LENGTH
import com.wahyusembiring.auth.PasswordValidationError
import com.wahyusembiring.auth.R
import com.wahyusembiring.auth.ReEnterPasswordValidationError
import com.wahyusembiring.auth.login.LoginScreenUIEvent
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.common.util.CollectAsOneTimeEvent
import com.wahyusembiring.ui.component.popup.alertdialog.error.ErrorAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.information.InformationAlertDialog
import com.wahyusembiring.ui.component.popup.alertdialog.loading.LoadingAlertDialog
import com.wahyusembiring.ui.theme.spacing

@Composable
fun RegisterScreen(
    viewModel: RegisterScreenViewModel,
    navController: NavController
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectAsOneTimeEvent(viewModel.navigationEvent) { navigationEvent ->
        when (navigationEvent) {

            RegisterScreenNavigationEvent.NavigateToLogin -> {
                navController.navigate(Screen.Login) {
                    launchSingleTop = true
                }
            }
        }
    }

    RegisterScreen(
        state = state,
        onEvent = viewModel::onEvent
    )

    for (popUp in state.popUps) {
        when (popUp) {
            is RegisterScreenPopUp.UserCreated -> {
                InformationAlertDialog(
                    title = stringResource(R.string.success),
                    message = stringResource(R.string.user_created_successfully),
                    buttonText = stringResource(R.string.login),
                    onButtonClicked = {
                        viewModel.onEvent(RegisterScreenEvent.ToLoginScreenButtonClicked)
                    },
                    onDismissRequest = {
                        viewModel.onEvent(RegisterScreenEvent.DismissPopUp(popUp))
                    }
                )
            }

            is RegisterScreenPopUp.Error -> {
                ErrorAlertDialog(
                    message = popUp.errorMessage,
                    buttonText = stringResource(R.string.ok),
                    onButtonClicked = {
                        viewModel.onEvent(RegisterScreenEvent.DismissPopUp(popUp))
                    },
                    onDismissRequest = {
                        viewModel.onEvent(RegisterScreenEvent.DismissPopUp(popUp))
                    }
                )
            }

            RegisterScreenPopUp.Loading -> {
                LoadingAlertDialog(stringResource(R.string.loading))
            }
        }
    }
}

@Composable
private fun RegisterScreen(
    state: RegisterScreenState,
    onEvent: (RegisterScreenEvent) -> Unit
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
                    onEvent(RegisterScreenEvent.LoginAsGuestButtonClicked)
                }
            ) {
                Text(
                    text = stringResource(R.string.skip)
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
                        text = stringResource(R.string.create_new_account),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.Medium, MaterialTheme.spacing.Large),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = stringResource(R.string.creating_a_new_account_or_skip_to_signing_in_as_a_guest),
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
                            onEvent(RegisterScreenEvent.EmailChanged(it))
                        },
                        label = { Text(text = stringResource(R.string.email)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        maxLines = 1,
                        singleLine = true,
                        isError = state.emailError != null,
                        supportingText = {
                            state.emailError?.let {
                                Text(
                                    text = when (it) {
                                        EmailValidationError.Empty -> stringResource(R.string.email_cannot_be_empty)
                                        EmailValidationError.Invalid -> stringResource(R.string.invalid_email_format)
                                    }
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Medium),
                        value = state.password,
                        onValueChange = {
                            onEvent(RegisterScreenEvent.PasswordChanged(it))
                        },
                        label = { Text(text = stringResource(R.string.password)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        maxLines = 1,
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = state.passwordError != null,
                        supportingText = {
                            state.passwordError?.let {
                                Text(
                                    text = when (it) {
                                        PasswordValidationError.Empty -> stringResource(R.string.password_cannot_be_empty)
                                        PasswordValidationError.TooShort -> stringResource(
                                            R.string.password_must_be_at_least_characters,
                                            MIN_PASSWORD_LENGTH
                                        )
                                    }
                                )
                            }
                        }
                    )
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.spacing.Medium),
                        value = state.confirmedPassword,
                        onValueChange = {
                            onEvent(RegisterScreenEvent.ConfirmedPasswordChanged(it))
                        },
                        label = { Text(text = stringResource(R.string.re_enter_password)) },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        maxLines = 1,
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        isError = state.confirmedPasswordError != null,
                        supportingText = {
                            state.confirmedPasswordError?.let {
                                Text(
                                    text = when (it) {
                                        ReEnterPasswordValidationError.Empty -> stringResource(R.string.password_cannot_be_empty)
                                        ReEnterPasswordValidationError.NotMatch -> stringResource(R.string.password_not_match)
                                    }
                                )
                            }
                        }
                    )
                    Button(
                        modifier = Modifier.padding(vertical = MaterialTheme.spacing.Large),
                        onClick = {
                            onEvent(RegisterScreenEvent.RegisterButtonClicked)
                        },
                    ) {
                        Text(text = stringResource(R.string.create_account))
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
                                    onEvent(RegisterScreenEvent.LoginWithGoogleButtonClicked(context))
                                },
                            painter = painterResource(id = R.drawable.google),
                            contentDescription = stringResource(R.string.google)
                        )
                        Spacer(modifier = Modifier.width(MaterialTheme.spacing.Medium))
                        Image(
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    onEvent(
                                        RegisterScreenEvent.LoginWithFacebookButtonClicked(
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
                            text = stringResource(R.string.already_have_an_account),
                            style = MaterialTheme.typography.bodySmall
                        )
                        TextButton(
                            onClick = {
                                onEvent(RegisterScreenEvent.ToLoginScreenButtonClicked)
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.login_here),
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