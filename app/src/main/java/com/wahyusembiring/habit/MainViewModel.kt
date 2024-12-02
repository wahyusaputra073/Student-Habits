package com.wahyusembiring.habit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wahyusembiring.common.navigation.Screen
import com.wahyusembiring.data.Result
import com.wahyusembiring.data.repository.AuthRepository
import com.wahyusembiring.data.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository,
) : ViewModel() {

    companion object {
        private const val TAG = "MainViewModel"
        private const val MINIMAL_SPLASH_SCREEN_DURATION = 3000L
    }

    private val _isAppReady = MutableStateFlow(false)
    val isAppReady: StateFlow<Boolean> = _isAppReady

    private val _startDestination: MutableStateFlow<Screen> = MutableStateFlow(Screen.Blank)

    val startDestination: StateFlow<Screen> = _startDestination
//    val startDestination: StateFlow<Screen> = flow<Screen> { }
//        .stateIn(viewModelScope, SharingStarted.Eagerly, Screen.Login)

    val currentUser = authRepository.currentUser.onEach {
//        if (it != null) cloudToLocalSync()
    }

    init {
        viewModelScope.launch { initializeApp() }
    }

    private suspend fun initializeApp() {
        coroutineScope {
            launch { delay(MINIMAL_SPLASH_SCREEN_DURATION) }
//            launch { cloudToLocalSync() }
            launch { setupStartDestination() }
            _isAppReady.value = true
        }
    }

    private suspend fun setupStartDestination(): Unit = coroutineScope {
        val isOnBoardingCompleted = async {
            when (val result = dataStoreRepository.readOnBoardingState().last()) {
                is Result.Loading -> { throw IllegalStateException("Expecting Error or Success as last emitted value but got Loading") }
                is Result.Error -> { throw result.throwable }
                is Result.Success -> { result.data.first() }
            }
        }.await()
        val isUserLoggedIn = async {
            authRepository.currentUser.first() != null
        }.await()
        val startDestination = when {
            isOnBoardingCompleted && isUserLoggedIn -> Screen.Overview
            isOnBoardingCompleted -> Screen.Login
            else -> Screen.OnBoarding
        }
        _startDestination.value = startDestination
    }

}