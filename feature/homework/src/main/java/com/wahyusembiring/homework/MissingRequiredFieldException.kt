package com.wahyusembiring.homework

sealed class MissingRequiredFieldException : Exception() {
   class Title : MissingRequiredFieldException()
   class Subject : MissingRequiredFieldException()
}