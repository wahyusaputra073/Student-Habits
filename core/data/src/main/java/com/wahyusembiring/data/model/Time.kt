package com.wahyusembiring.data.model

import kotlinx.serialization.Serializable

@Deprecated("Use LocalTime instead")
@Serializable
data class Time(
   val hour: Int,
   val minute: Int
)