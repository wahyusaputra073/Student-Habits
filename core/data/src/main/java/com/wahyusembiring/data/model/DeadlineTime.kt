package com.wahyusembiring.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeadlineTime(
    val hour: Int,
    val minute: Int
)

