package com.wahyusembiring.subject.screen.create

sealed class MissingRequiredFieldException : Exception() {
    class SubjectName : MissingRequiredFieldException()
    class Room : MissingRequiredFieldException()
    class Lecture : MissingRequiredFieldException()
}