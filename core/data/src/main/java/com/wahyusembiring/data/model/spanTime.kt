package com.wahyusembiring.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SpanTime(
    val startTime: Time,
    val endTime: Time
)