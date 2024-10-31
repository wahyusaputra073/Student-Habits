package com.wahyusembiring.reminder

sealed class MissingRequiredFieldException : Exception() {
   class Title : MissingRequiredFieldException()
   class Date : MissingRequiredFieldException()
   class Time : MissingRequiredFieldException()
}