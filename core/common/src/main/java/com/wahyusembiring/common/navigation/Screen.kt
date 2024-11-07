package com.wahyusembiring.common.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {

    @Serializable
    data object Blank : Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object Overview : Screen()

    @Serializable
    data class CreateHomework(val homeworkId: Int = -1) : Screen()

    @Serializable
    data class CreateReminder(val reminderId: Int = -1) : Screen()

    @Serializable
    data class CreateExam(val examId: Int = -1) : Screen()

    @Serializable
    data class CreateSubject(val subjectId: Int = -1) : Screen()

    @Serializable
    data object ThesisSelection : Screen()

    @Serializable
    data class ThesisPlanner(val thesisId: Int) : Screen()

    @Serializable
    data object Calendar : Screen()

    @Serializable
    data object OnBoarding : Screen()

    @Serializable
    data object Subject : Screen()

    @Serializable
    data object Lecture : Screen()

    @Serializable
    data class AddLecturer(val lecturerId: Int = -1) : Screen()

    @Serializable
    data object Settings : Screen()
}