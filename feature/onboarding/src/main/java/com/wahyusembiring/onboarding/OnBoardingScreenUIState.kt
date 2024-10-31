package com.wahyusembiring.onboarding

import com.wahyusembiring.onboarding.model.OnBoardingModel

data class OnBoardingScreenUIState(

    val models: List<OnBoardingModel> = listOf(
        OnBoardingModel.First,
        OnBoardingModel.Second,
        OnBoardingModel.Third
    )

)