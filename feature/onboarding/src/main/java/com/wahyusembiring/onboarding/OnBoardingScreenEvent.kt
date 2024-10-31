package com.wahyusembiring.onboarding

sealed class OnBoardingScreenUIEvent {
    data object OnCompleted : OnBoardingScreenUIEvent()
}

sealed class OnBoardingScreenNavigationEvent {
    data object NavigateToLogin : OnBoardingScreenNavigationEvent()
}