package com.wahyusembiring.auth

sealed class EmailValidationError {
    data object Empty : EmailValidationError()
    data object Invalid : EmailValidationError()
}

sealed class PasswordValidationError {
    data object Empty : PasswordValidationError()
    data object TooShort : PasswordValidationError()
}

sealed class ReEnterPasswordValidationError {
    data object Empty : ReEnterPasswordValidationError()
    data object NotMatch : ReEnterPasswordValidationError()
}