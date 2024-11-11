package com.wahyusembiring.onboarding

import com.wahyusembiring.onboarding.model.OnBoardingModel
import com.wahyusembiring.ui.util.UIText

data class OnBoardingScreenUIState(

    val models: List<OnBoardingModel> = listOf(
        OnBoardingModel.First,
        OnBoardingModel.Second,
        OnBoardingModel.Third
    ),

    val popUps: List<OnBoardingScreenPopUp> = emptyList()

)

sealed class OnBoardingScreenPopUp {
    data object Loading : OnBoardingScreenPopUp()
    data class Error(val message: UIText) : OnBoardingScreenPopUp()
}