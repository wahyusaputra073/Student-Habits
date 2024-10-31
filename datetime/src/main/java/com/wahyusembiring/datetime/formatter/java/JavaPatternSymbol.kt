package com.wahyusembiring.datetime.formatter.java

@Suppress("MemberVisibilityCanBePrivate")
internal object JavaPatternSymbol {

    /**
     * Day of week in short format.
     *
     * Ex: Mon, Tue, Wed, Thu, Fri, Sat, Sun
     */
    const val DAY_OF_WEEK_SHORT = "EEE"

    /**
     * Day of week in full format.
     *
     * Ex: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
     */
    const val DAY_OF_WEEK = "EEEE"

    /**
     * Day of month.
     *
     * Ex: 1, 2, 3, 4, 5, 6, 7, 8, 9, ..., 30, 31
     */
    const val DAY_OF_MONTH = "d"

    /**
     * Day of month with zero prefix.
     *
     * Ex: 01, 02, 03, 04, 05, 06, 07, 08, 09, ..., 30, 31
     */
    const val DAY_OF_MONTH_WITH_ZERO = "dd"

    /**
     * Month of year.
     *
     * Ex: 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
     */
    const val MONTH_OF_YEAR = "M"

    /**
     * Month of year with zero prefix.
     *
     * Ex: 01, 02, 03, 04, 05, 06, 07, 08, 09, 10, 11, 12
     */
    const val MONTH_OF_YEAR_WITH_ZERO = "MM"

    /**
     * The Month's name of year in short format.
     *
     * Ex: Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec
     */
    const val MONTH_NAME_SHORT = "MMM"

    /**
     * The Month's name of year in full format.
     *
     * Ex: January, February, March, April, May, June, July, August, September, October, November, December
     */
    const val MONTH_NAME = "MMMM"

    /**
     * Year in full format.
     *
     * Ex: 1998, 1999, ..., 2023, 2024
     */
    const val YEAR = "yyyy"

    /**
     * Year in short format.
     *
     * Ex: 98, 99, ..., 23, 24
     */
    const val YEAR_SHORT = "yy"

    /**
     * Hour in 24-hour format.
     *
     * Ex: 0, 1, 2, ..., 23
     */
    const val HOUR_IN_DAY = "H"

    /**
     * Hour in 24-hour format with zero prefix.
     *
     * Ex: 00, 01, 02, ..., 23
     */
    const val HOUR_IN_DAY_WITH_ZERO = "HH"

    /**
     * Minute in hour.
     *
     * Ex: 0, 1, 2, ..., 59
     */
    const val MINUTE_IN_HOUR = "m"

    /**
     * Minute in hour with zero prefix.
     *
     * Ex: 00, 01, 02, ..., 59
     */
    const val MINUTE_IN_HOUR_WITH_ZERO = "mm"

}