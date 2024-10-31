package com.wahyusembiring.datetime.formatter.java

import android.os.Build
import androidx.annotation.RequiresApi
import com.wahyusembiring.datetime.formatter.Formatter
import com.wahyusembiring.datetime.formatter.FormattingStyle
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Date
import java.util.Locale

internal class JavaFormatter : Formatter {

    override fun format(
        instant: Instant,
        pattern: String
    ): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            format(
                instant = instant,
                pattern = pattern,
                locale = Locale.getDefault()
            )
        } else {
            formatLegacy(
                date = Date(instant.toEpochMilliseconds()),
                pattern = pattern,
                locale = Locale.getDefault()
            )
        }
    }

    override fun format(instant: Instant, formattingStyle: FormattingStyle): String {
        val pattern = when (formattingStyle) {
            FormattingStyle.INDO_SHORT -> JavaFormatterPattern.INDO_SHORT
            FormattingStyle.INDO_MEDIUM -> JavaFormatterPattern.INDO_MEDIUM
            FormattingStyle.INDO_FULL -> JavaFormatterPattern.INDO_FULL
            FormattingStyle.INDO_FULL_SHORT -> JavaFormatterPattern.INDO_FULL_SHORT
        }
        return format(instant, pattern)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun format(
        instant: Instant,
        pattern: String,
        locale: Locale
    ): String {
        val javaInstant = instant.toJavaInstant()
        val formatter = DateTimeFormatter.ofPattern(pattern).withLocale(locale)
        return javaInstant.atZone(ZoneId.systemDefault()).format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun format(
        localDateTime: LocalDateTime,
        locale: Locale
    ): String {
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(locale)
        return localDateTime.format(formatter)
    }

    private fun formatLegacy(
        date: Date,
        pattern: String,
        locale: Locale
    ): String {
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(date)
    }

}