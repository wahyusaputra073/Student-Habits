package com.wahyusembiring.onboarding.model

import androidx.annotation.DrawableRes
import com.wahyusembiring.onboarding.R

sealed class OnBoardingModel(
    @DrawableRes val image: Int,
    val title: String,
    val description: String
) {

    data object First : OnBoardingModel(
        image = R.drawable.onboarding_schedule,
        title = "Stay on top of your schedule",
        description = "Always know your deadlines, plan your days with an integrated calendar"
    )

    data object Second : OnBoardingModel(
        image = R.drawable.onboarding_productivity,
        title = "Boost your productivity",
        description = "Get task reminders, create important note, and track your study progress"
    )

    data object Third : OnBoardingModel(
        image = R.drawable.onboarding_time_management,
        title = "Ace your exams",
        description = "Create effective study schedule, track your learning progress, and plan your exams"
    )


}