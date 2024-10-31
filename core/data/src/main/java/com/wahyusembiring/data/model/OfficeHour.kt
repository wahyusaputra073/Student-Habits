package com.wahyusembiring.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OfficeHour(
   val day: Int,
   val startTime: Time,
   val endTime: Time
)