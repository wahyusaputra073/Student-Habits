package com.wahyusembiring.exam

sealed class MissingRequiredFieldException : Exception() {
   class Title : MissingRequiredFieldException()
   class Subject : MissingRequiredFieldException()
}