package com.wahyusembiring.auth.login

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.http.HttpException
import android.util.Log
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "LoginScreenViewModel"
    }

    private val _state = MutableStateFlow(LoginScreenUIState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<LoginScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect {
                if (it != null) {
                    _navigationEvent.send(LoginScreenNavigationEvent.NavigateToHomeScreen)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _navigationEvent.close()
    }

    fun onUIEvent(event: LoginScreenUIEvent) {
        when (event) {
            is LoginScreenUIEvent.OnEmailChange -> onEmailChange(event.email)
            is LoginScreenUIEvent.OnLoginButtonClick -> onLoginButtonClick()
            is LoginScreenUIEvent.OnPasswordChange -> onPasswordChange(event.password)
            is LoginScreenUIEvent.OnPopupDismissRequest -> onPopupDismissRequest(event.popUp)
            is LoginScreenUIEvent.OnLoginSkipButtonClick -> onLoginSkipButtonClick()
            is LoginScreenUIEvent.OnLoginWithFacebookButtonClick -> onLoginWithFacebookButtonClick(event.activityResultRegistryOwner)
            is LoginScreenUIEvent.OnLoginWithGoogleButtonClick -> onLoginWithGoogleButtonClick(event.context)
            is LoginScreenUIEvent.OnRegisterHereButtonClick -> onRegisterHereButtonClick()
        }
    }

    private fun onRegisterHereButtonClick() {
        _navigationEvent.trySend(LoginScreenNavigationEvent.NavigateToRegisterScreen)
    }

    private fun onLoginSkipButtonClick() {
        viewModelScope.launch {
            val request = authRepository.signInAnonymously()
            request.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUpStack = it.popUpStack + LoginScreenPopUp.SignInLoading)
                        }
                    }

                    is Result.Error -> {
                        val throwable = result.throwable
                        Log.e(TAG, "Login failed: ${throwable.message}", throwable)
                        _state.update {
                            it.copy(
                                popUpStack = it.popUpStack
                                    .minus(LoginScreenPopUp.SignInLoading)
                                    .plus(LoginScreenPopUp.SignInFailed)
                            )
                        }
                    }

                    is Result.Success -> {
                        _state.update {
                            it.copy(popUpStack = it.popUpStack - LoginScreenPopUp.SignInLoading)
                        }
                    }
                }
            }
        }
    }

    private fun onLoginWithFacebookButtonClick(activityResultRegistryOwner: ActivityResultRegistryOwner?) {
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
                            it.copy(popUpStack = it.popUpStack + LoginScreenPopUp.SignInLoading)
                        }
                    }

                    is Result.Error -> {
                        val throwable = result.throwable
                        Log.e(TAG, "Login failed: ${throwable.message}", throwable)
                        _state.update {
                            it.copy(
                                popUpStack = it.popUpStack
                                    .minus(LoginScreenPopUp.SignInLoading)
                                    .plus(LoginScreenPopUp.SignInFailed)
                            )
                        }
                    }

                    is Result.Success -> {
                        _state.update {
                            it.copy(popUpStack = it.popUpStack - LoginScreenPopUp.SignInLoading)
                        }
                    }
                }
            }
        }
    }

    private fun onLoginWithGoogleButtonClick(context: Context) {
        viewModelScope.launch {
            val request = authRepository.signInWithGoogle(context)
            request.collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUpStack = it.popUpStack + LoginScreenPopUp.SignInLoading)
                        }
                    }

                    is Result.Error -> {
                        val throwable = result.throwable
                        Log.e(TAG, "Login failed: ${throwable.message}", throwable)
                        _state.update {
                            it.copy(
                                popUpStack = it.popUpStack
                                    .minus(LoginScreenPopUp.SignInLoading)
                                    .plus(LoginScreenPopUp.SignInFailed)
                            )
                        }
                    }

                    is Result.Success -> {
                        _state.update {
                            it.copy(popUpStack = it.popUpStack - LoginScreenPopUp.SignInLoading)
                        }
                    }
                }
            }
        }
    }


    private fun onEmailChange(email: String) {
        _state.update { it.copy(email = email) }
    }

    private fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password) }
    }

    private fun onLoginButtonClick() = viewModelScope.launch(Dispatchers.IO) {
        val request = authRepository.signInWithEmailAndPassword(
            email = _state.value.email,
            password = _state.value.password
        )
        request.collect { result ->
            when (result) {
                is Result.Loading -> {
                    _state.update {
                        it.copy(popUpStack = it.popUpStack + LoginScreenPopUp.SignInLoading)
                    }
                }

                is Result.Error -> {
                    val throwable = result.throwable
                    Log.e(TAG, "Login failed: ${throwable.message}", throwable)
                    _state.update {
                        it.copy(
                            popUpStack = it.popUpStack
                                .minus(LoginScreenPopUp.SignInLoading)
                                .plus(LoginScreenPopUp.SignInFailed)
                        )
                    }
                }

                is Result.Success -> {
                    _state.update {
                        it.copy(popUpStack = it.popUpStack - LoginScreenPopUp.SignInLoading)
                    }
//                    _navigationEvent.send(LoginScreenNavigationEvent.NavigateToHomeScreen)
                }
            }
        }
    }

    private fun onPopupDismissRequest(popUp: LoginScreenPopUp) {
        _state.update {
            it.copy(popUpStack = it.popUpStack - popUp)
        }
    }
}