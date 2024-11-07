package com.wahyusembiring.settings

data class SettingScreenState(
    val popUps: List<SettingScreenPopUp> = emptyList()
)

sealed class SettingScreenEvent {
    data object LogoutButtonClicked: SettingScreenEvent()
    data object LogoutConfirmed: SettingScreenEvent()
    data class DismissPopUp(val popUp: SettingScreenPopUp): SettingScreenEvent()
    data object HamburgerMenuButtonClicked: SettingScreenEvent()
}

sealed class SettingScreenPopUp {
    data object Loading : SettingScreenPopUp()
    data object LogoutConfirmation : SettingScreenPopUp()
    data class Error(val errorMessages: String) : SettingScreenPopUp()
}

sealed class SettingScreenNavigationEvent {
    data object NavigateToLogin: SettingScreenNavigationEvent()
}