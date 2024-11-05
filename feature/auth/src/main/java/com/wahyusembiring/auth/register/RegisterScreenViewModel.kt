package com.wahyusembiring.auth.register

import android.content.Context
import android.util.Log
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    companion object {
        private const val TAG = "RegisterScreenViewModel"
    }

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
            is RegisterScreenEvent.LoginAsGuestButtonClicked -> onLoginAsGuestButtonClicked()
            is RegisterScreenEvent.LoginWithFacebookButtonClicked -> onLoginWithFacebookButtonClicked(event.activityResultRegistryOwner)
            is RegisterScreenEvent.LoginWithGoogleButtonClicked -> onLoginWithGoogleButtonClicked(event.context)
        }
    }

    private fun onLoginWithGoogleButtonClicked(context: Context) {
        viewModelScope.launch {
            val request = authRepository.signInWithGoogle(context)
            request.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + RegisterScreenPopUp.Loading)
                        }
                    }

                    is Result.Error -> {
                        val throwable = result.throwable
                        Log.e(TAG, "Login failed: ${throwable.message}", throwable)
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(RegisterScreenPopUp.Loading)
                                    .plus(RegisterScreenPopUp.Error(result.throwable.message ?: ""))
                            )
                        }
                    }

                    is Result.Success -> {
                        _state.update {
                            it.copy(popUps = it.popUps - RegisterScreenPopUp.Loading)
                        }
                    }
                }
            }
        }
    }

    private fun onLoginWithFacebookButtonClicked(activityResultRegistryOwner: ActivityResultRegistryOwner?) {
        if (activityResultRegistryOwner == null) {
            Log.e(TAG, "Attempt to login with facebook but activity result registry owner is null")
            return
        }
        val result = authRepository.signInWithFacebook(activityResultRegistryOwner)
        viewModelScope.launch {
            result.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + RegisterScreenPopUp.Loading)
                        }
                    }

                    is Result.Error -> {
                        val throwable = result.throwable
                        Log.e(TAG, "Login failed: ${throwable.message}", throwable)
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(RegisterScreenPopUp.Loading)
                                    .plus(RegisterScreenPopUp.Error(result.throwable.message ?: ""))
                            )
                        }
                    }

                    is Result.Success -> {
                        _state.update {
                            it.copy(popUps = it.popUps - RegisterScreenPopUp.Loading)
                        }
                    }
                }
            }
        }
    }

    private fun onLoginAsGuestButtonClicked() {
        viewModelScope.launch {
            val request = authRepository.signInAnonymously()
            request.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + RegisterScreenPopUp.Loading)
                        }
                    }

                    is Result.Error -> {
                        val throwable = result.throwable
                        Log.e(TAG, "Login failed: ${throwable.message}", throwable)
                        _state.update {
                            it.copy(
                                popUps = it.popUps
                                    .minus(RegisterScreenPopUp.Loading)
                                    .plus(RegisterScreenPopUp.Error(result.throwable.message ?: ""))
                            )
                        }
                    }

                    is Result.Success -> {
                        _state.update {
                            it.copy(popUps = it.popUps - RegisterScreenPopUp.Loading)
                        }
                    }
                }
            }
        }
    }

    private fun onToLoginScreenButtonClicked() {
        _navigationEvent.trySend(RegisterScreenNavigationEvent.NavigateToLogin)
    }

    private fun onRegisterButtonClicked() {
        //TODO: Validate user input
        viewModelScope.launch {
            val request = authRepository.createUserWithEmailAndPassword(
                email = _state.value.email,
                password = _state.value.password
            )
            request.collect { result ->
                when (result) {
                    is Result.Error -> {
                        _state.update { it.copy(popUps = it.popUps + RegisterScreenPopUp.Error(result.throwable.message ?: "")) }
                    }
                    is Result.Loading -> {
                        _state.update { it.copy(popUps = it.popUps + RegisterScreenPopUp.Loading) }
                    }
                    is Result.Success -> {
                        _state.update { it.copy(popUps = it.popUps + RegisterScreenPopUp.UserCreated) }
                    }
                }
            }
        }
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
