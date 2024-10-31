package com.wahyusembiring.auth.login

import android.app.Activity
import android.content.Context


data class LoginScreenUIState(
    val email: String = "",
    val password: String = "",
    val popUpStack: List<LoginScreenPopUp> = emptyList(),
)

sealed class LoginScreenUIEvent {
    data class OnEmailChange(val email: String) : LoginScreenUIEvent()
    data class OnPasswordChange(val password: String) : LoginScreenUIEvent()
    data object OnLoginButtonClick : LoginScreenUIEvent()
    data class OnPopupDismissRequest(val popUp: LoginScreenPopUp) : LoginScreenUIEvent()
    data object OnLoginSkipButtonClick : LoginScreenUIEvent()
    data class OnLoginWithGoogleButtonClick(val context: Context) : LoginScreenUIEvent()
    data object OnLoginWithFacebookButtonClick : LoginScreenUIEvent()
    data object OnRegisterHereButtonClick : LoginScreenUIEvent()
}

sealed class LoginScreenNavigationEvent {
    data object NavigateToHomeScreen : LoginScreenNavigationEvent()
}

sealed class LoginScreenPopUp {
    data object SignInLoading : LoginScreenPopUp()
    data object SignInFailed : LoginScreenPopUp()
    data object CommonLoading : LoginScreenPopUp()
}


