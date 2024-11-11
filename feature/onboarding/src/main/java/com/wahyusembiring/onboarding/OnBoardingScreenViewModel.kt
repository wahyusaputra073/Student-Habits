package com.wahyusembiring.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingScreenViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingScreenUIState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvent = Channel<OnBoardingScreenNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        viewModelScope.launch {
            dataStoreRepository.readOnBoardingState().collect { result ->
                when (result) {
                    is Result.Loading -> {}
                    is Result.Error -> { throw result.throwable }
                    is Result.Success -> {
                        result.data.collect { completed ->
                            if (completed) {
                                _navigationEvent.send(OnBoardingScreenNavigationEvent.NavigateToLogin)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onUIEvent(event: OnBoardingScreenUIEvent) {
        when (event) {
            is OnBoardingScreenUIEvent.OnCompleted -> {
                saveOnBoardingState(completed = true)
            }
            is OnBoardingScreenUIEvent.OnDismissPopUp -> onDismissPopUp(event.popUp)
        }
    }

    private fun onDismissPopUp(popUp: OnBoardingScreenPopUp) {
        _uiState.update { it.copy(popUps = it.popUps - popUp) }
    }

    private fun saveOnBoardingState(completed: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveOnBoardingState(completed = completed).collect { result ->
                when (result) {
                    is Result.Loading -> {}
                    is Result.Error -> { throw result.throwable }
                    is Result.Success -> {}
                }
            }
        }
    }

}