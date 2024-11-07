package com.wahyusembiring.settings

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
class SettingScreenViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {

    companion object {
        private const val TAG = "SettingScreenViewModel"
    }

    init {
        viewModelScope.launch {
            authRepository.currentUser.collect {
                if (it == null) {
                    _navigationEvent.send(SettingScreenNavigationEvent.NavigateToLogin)
                }
            }
        }
    }

    private val _state = MutableStateFlow(SettingScreenState())
    val state = _state.asStateFlow()

    private val _navigationEvent = Channel<SettingScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _uiOneTimeEvent = Channel<SettingScreenEvent>()
    val uiOneTimeEvent = _uiOneTimeEvent.receiveAsFlow()

    fun onEvent(event: SettingScreenEvent) {
        when (event) {
            is SettingScreenEvent.LogoutButtonClicked -> onLogoutButtonClicked()
            is SettingScreenEvent.LogoutConfirmed -> onLogoutConfirmed()
            is SettingScreenEvent.DismissPopUp -> onDismissPopUp(event.popUp)
            is SettingScreenEvent.HamburgerMenuButtonClicked -> onHamburgerMenuButtonClicked()
        }
    }

    private fun onHamburgerMenuButtonClicked() {
        _uiOneTimeEvent.trySend(SettingScreenEvent.HamburgerMenuButtonClicked)
    }

    private fun onDismissPopUp(popUp: SettingScreenPopUp) {
        _state.update {
            it.copy(popUps = it.popUps - popUp)
        }
    }

    private fun onLogoutConfirmed() {
        viewModelScope.launch {
            authRepository.logout().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        _state.update {
                            it.copy(popUps = it.popUps + SettingScreenPopUp.Loading)
                        }
                    }
                    is Result.Error -> {
                        _state.update {
                            it.copy(popUps = it.popUps + SettingScreenPopUp.Error(result.throwable.message ?: "Unknown error"))
                        }
                    }
                    is Result.Success -> {}
                }
            }
        }
    }

    private fun onLogoutButtonClicked() {
        _state.update {
            it.copy(popUps = it.popUps + SettingScreenPopUp.LogoutConfirmation)
        }
    }

}