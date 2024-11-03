package com.wahyusembiring.auth.register

import android.content.Context
import androidx.activity.result.ActivityResultRegistryOwner

data class RegisterScreenState(
    val email: String = "",
    val password: String = "",
    val confirmedPassword: String = "",
    val popUps: List<RegisterScreenPopUp> = emptyList()
)

sealed class RegisterScreenEvent {
    data class EmailChanged(val email: String) : RegisterScreenEvent()
    data class PasswordChanged(val password: String) : RegisterScreenEvent()
    data class ConfirmedPasswordChanged(val confirmedPassword: String) : RegisterScreenEvent()
    data object RegisterButtonClicked : RegisterScreenEvent()
    data object ToLoginScreenButtonClicked : RegisterScreenEvent()
    data class DismissPopUp(val popUp: RegisterScreenPopUp) : RegisterScreenEvent()
    data object LoginAsGuestButtonClicked : RegisterScreenEvent()
    data class LoginWithGoogleButtonClicked(val context: Context) : RegisterScreenEvent()
    data class LoginWithFacebookButtonClicked(val activityResultRegistryOwner: ActivityResultRegistryOwner?) : RegisterScreenEvent()
}

sealed class RegisterScreenPopUp {
    data object UserCreated : RegisterScreenPopUp()
    data class Error(val errorMessage: String) : RegisterScreenPopUp()
    data object Loading : RegisterScreenPopUp()
}

sealed class RegisterScreenNavigationEvent {
    data object NavigateToLogin : RegisterScreenNavigationEvent()
}