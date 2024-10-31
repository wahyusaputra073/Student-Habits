package com.wahyusembiring.data.exception

class UserIsNotSignInException : Exception() {
    override val message: String
        get() = "Expecting user to be sign in, but null is returned by AuthRepository.currentUser"
}