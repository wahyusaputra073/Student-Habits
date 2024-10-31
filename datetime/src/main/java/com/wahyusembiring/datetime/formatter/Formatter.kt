package com.wahyusembiring.datetime.formatter

import kotlinx.datetime.Instant

internal interface Formatter {

    fun format(instant: Instant, pattern: String): String

    fun format(instant: Instant, formattingStyle: FormattingStyle): String

}