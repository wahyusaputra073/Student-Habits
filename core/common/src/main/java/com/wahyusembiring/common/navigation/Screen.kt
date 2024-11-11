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
    data class CreateHomework(val homeworkId: String = "-1") : Screen()

    @Serializable
    data class CreateReminder(val reminderId: String = "-1") : Screen()

    @Serializable
    data class CreateExam(val examId: String = "-1") : Screen()

    @Serializable
    data class CreateSubject(val subjectId: String = "-1") : Screen()

    @Serializable
    data object ThesisSelection : Screen()

    @Serializable
    data class ThesisPlanner(val thesisId: String) : Screen()

    @Serializable
    data object Calendar : Screen()

    @Serializable
    data object OnBoarding : Screen()

    @Serializable
    data object Subject : Screen()

    @Serializable
    data object Lecture : Screen()

    @Serializable
    data class AddLecturer(val lecturerId: String = "-1") : Screen()

    @Serializable
    data object Settings : Screen()
}