package com.wahyusembiring.exam

sealed class MissingRequiredFieldException : Exception() {
   class Title : MissingRequiredFieldException()
   class Date : MissingRequiredFieldException()
   class Time : MissingRequiredFieldException()
   class Subject : MissingRequiredFieldException()
}