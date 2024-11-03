package com.wahyusembiring.auth.register

import androidx.lifecycle.ViewModel
import com.wahyusembiring.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    private val _state = MutableStateFlow(RegisterScreenState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<RegisterScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun onEvent(event: RegisterScreenEvent) {
        when (event) {
            is RegisterScreenEvent.ConfirmedPasswordChanged -> onConfirmedPasswordChanged(event.confirmedPassword)
            is RegisterScreenEvent.DismissPopUp -> onDismissPopUp(event.popUp)
            is RegisterScreenEvent.EmailChanged -> onEmailChanged(event.email)
            is RegisterScreenEvent.PasswordChanged -> onPasswordChanged(event.password)
            is RegisterScreenEvent.RegisterButtonClicked -> onRegisterButtonClicked()
            is RegisterScreenEvent.ToLoginScreenButtonClicked -> onToLoginScreenButtonClicked()
            is RegisterScreenEvent.LoginAsGuestButtonClicked -> TODO()
            is RegisterScreenEvent.LoginWithFacebookButtonClicked -> TODO()
            is RegisterScreenEvent.LoginWithGoogleButtonClicked -> TODO()
        }
    }

    private fun onToLoginScreenButtonClicked() {
        _navigationEvent.trySend(RegisterScreenNavigationEvent.NavigateToLogin)
    }

    private fun onRegisterButtonClicked() {
        TODO()
    }

    private fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password) }
    }

    private fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email) }
    }

    private fun onDismissPopUp(popUp: RegisterScreenPopUp) {
        _state.update { it.copy(popUps = it.popUps - popUp) }
    }

    private fun onConfirmedPasswordChanged(confirmedPassword: String) {
        _state.update { it.copy(confirmedPassword = confirmedPassword) }
    }

}
