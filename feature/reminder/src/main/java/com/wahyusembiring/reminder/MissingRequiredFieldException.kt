package com.wahyusembiring.reminder

sealed class MissingRequiredFieldException : Exception() {
   class Title : MissingRequiredFieldException()
   class DateTime : MissingRequiredFieldException()
}