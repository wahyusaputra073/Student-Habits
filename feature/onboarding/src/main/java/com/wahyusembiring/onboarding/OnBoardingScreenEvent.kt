package com.wahyusembiring.onboarding

sealed class OnBoardingScreenUIEvent {
    data object OnCompleted : OnBoardingScreenUIEvent()
    data class OnDismissPopUp(val popUp: OnBoardingScreenPopUp) : OnBoardingScreenUIEvent()
}

sealed class OnBoardingScreenNavigationEvent {
    data object NavigateToLogin : OnBoardingScreenNavigationEvent()
}