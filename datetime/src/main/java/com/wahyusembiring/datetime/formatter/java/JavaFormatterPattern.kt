package com.wahyusembiring.datetime.formatter.java

import com.wahyusembiring.datetime.formatter.java.JavaPatternSymbol.DAY_OF_MONTH
import com.wahyusembiring.datetime.formatter.java.JavaPatternSymbol.DAY_OF_MONTH_WITH_ZERO
import com.wahyusembiring.datetime.formatter.java.JavaPatternSymbol.DAY_OF_WEEK
import com.wahyusembiring.datetime.formatter.java.JavaPatternSymbol.DAY_OF_WEEK_SHORT
import com.wahyusembiring.datetime.formatter.java.JavaPatternSymbol.MONTH_NAME
import com.wahyusembiring.datetime.formatter.java.JavaPatternSymbol.MONTH_NAME_SHORT
import com.wahyusembiring.datetime.formatter.java.JavaPatternSymbol.YEAR
import com.wahyusembiring.datetime.formatter.java.JavaPatternSymbol.YEAR_SHORT

internal object JavaFormatterPattern {

    /**
     * Pre-defined date format.
     *
     * "[DAY_OF_MONTH]&nbsp[MONTH_NAME_SHORT]&nbsp[YEAR_SHORT]"
     *
     * Ex: 1 Jan 23
     */
    const val INDO_SHORT = "$DAY_OF_MONTH $MONTH_NAME_SHORT $YEAR_SHORT"

    /**
     * Pre-defined date format.
     *
     * "[DAY_OF_MONTH_WITH_ZERO]&nbsp[MONTH_NAME]&nbsp[YEAR]"
     *
     * Ex: 01 January 2023
     */
    const val INDO_MEDIUM = "$DAY_OF_MONTH_WITH_ZERO $MONTH_NAME $YEAR"

    /**
     * Pre-defined date format.
     * "[DAY_OF_WEEK],&nbsp[DAY_OF_MONTH_WITH_ZERO]&nbsp[MONTH_NAME]&nbsp[YEAR]"
     *
     * Ex: Monday, 01 January 2023
     */
    const val INDO_FULL = "$DAY_OF_WEEK, $DAY_OF_MONTH_WITH_ZERO $MONTH_NAME $YEAR"

    /**
     * Pre-defined date format.
     *
     * "[DAY_OF_WEEK_SHORT],&nbsp[DAY_OF_MONTH]&nbsp[MONTH_NAME_SHORT]&nbsp[YEAR_SHORT]"
     *
     * Ex: Mon, 1 Jan 23
     */
    const val INDO_FULL_SHORT =
        "$DAY_OF_WEEK_SHORT, $DAY_OF_MONTH $MONTH_NAME_SHORT $YEAR_SHORT"

}