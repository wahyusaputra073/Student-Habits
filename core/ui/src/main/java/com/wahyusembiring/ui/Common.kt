package com.wahyusembiring.ui

import com.wahyusembiring.ui.util.UIText
import java.time.Duration
import java.time.LocalDateTime
import java.time.Period
import java.time.temporal.TemporalAmount

sealed class ReminderOption {

    class Predefined(
        val displayName: UIText,
        val duration: TemporalAmount,
    ) : ReminderOption()

    class Custom(
        val dateTime: LocalDateTime,
    ) : ReminderOption()

    companion object {

        val dueReminderDefaultOptions = listOf(
            Predefined(
                UIText.StringResource(R.string._15_minutes_before),
                Duration.ofMinutes(15)
            ),
            Predefined(
                UIText.StringResource(R.string._30_minutes_before),
                Duration.ofMinutes(30)
            ),
            Predefined(
                UIText.StringResource(R.string._1_hour_before),
                Duration.ofHours(1)
            ),
        )

        val deadlineReminderDefaultOptions = listOf(
            Predefined(
                UIText.StringResource(R.string._1_day_before),
                Period.ofDays(1)
            ),
            Predefined(
                UIText.StringResource(R.string._2_days_before),
                Period.ofDays(2)
            ),
            Predefined(
                UIText.StringResource(R.string._3_days_before),
                Period.ofDays(3)
            ),
        )

    }
}