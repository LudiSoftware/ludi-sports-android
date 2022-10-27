package io.usys.report.utils

import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/* Date Utils */
//-VERIFIED-\\
fun convertStringToDate(date:String, pattern: String? = "yyyy-MM-d"): org.threeten.bp.LocalDate {
    return org.threeten.bp.LocalDate.parse(date, org.threeten.bp.format.DateTimeFormatter.ofPattern(pattern))
}
//-VERIFIED-\\
fun String.toDate(pattern: String? = "yyyy-MM-d"): org.threeten.bp.LocalDate {
    return org.threeten.bp.LocalDate.parse(this, org.threeten.bp.format.DateTimeFormatter.ofPattern(pattern))
}
//-VERIFIED-\\
fun org.threeten.bp.LocalDate.toMonthYearForFirebase() : String? {
    return this.toString().toMonthYearForFirebase()
}

//-VERIFIED-\\
fun getMonthYearForFirebase(): String {
    return SimpleDateFormat(FireHelper.SPOT_MONTH_DB, Locale.US).format(Date())
}

//-VERIFIED-\\
fun String.toMonthYearForFirebase(): String? {
    val toDate = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(this) ?: return null
    return SimpleDateFormat(FireHelper.SPOT_MONTH_DB, Locale.US).format(toDate) ?: return null
}

//-VERIFIED-\\
fun String.toFullDateString(): String? {
    val toDate = SimpleDateFormat("yyyy-MM-d", Locale.US).parse(this) ?: return null
    return SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US).format(toDate) ?: return null
}


//-VERIFIED-\\
fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}

/**
 * 1. isDateOld()
 * 2. isDateWithinTwoWeeks()
 **/

class DateUtils {

    companion object {
        var TIMEZONE_UTC = "CST"

        var format_FT = "yyyy-MM-d"
        var format_FULLDAY = "EEEE"
        var format_FULLDAY_MONTH_DAY_YEAR = "EEEE, MMMM dd yyyy"
        var format_MONTH_DAY_YEAR = "MMMM dd yyyy"
        var format_FULLDAY_MONTH_DAY = "EEEE, MMMM dd"
        var format_MONTH_DAY = "MMMM dd"
        var format_YEAR_MONTH_DAY = "yyyy-MM-dd"
        var format_DATE_TIME = "dd-MM-yyyy hh:mm"
        var format_DATE_MILITARY_TIME = "dd-MM-yyyy HH:mm"

        var format_MONTH_YEAR_for_FIREBASE = "MMMyyyy"
    }


    fun getDiff(toyBornTime: String?, schema: String?): Long? {
        val dateFormat = SimpleDateFormat(schema, Locale.US)
        try {
            val oldDate: Date = dateFormat.parse(toyBornTime) ?: Date()
            val currentDate = Date()
            val diff: Long = oldDate.time - currentDate.time
            val seconds = diff / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            return diff
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return java.lang.Long.valueOf(0)
    }

    fun getCurrentDayTime(): String? {
        val current: Locale = Locale.US
        return try {
            val formatter = SimpleDateFormat(format_DATE_TIME, current)
            formatter.timeZone = TimeZone.getDefault()
            formatter.format(Calendar.getInstance().time)
        } catch (e: Exception) {
            SimpleDateFormat(format_DATE_TIME, current)
                .format(Calendar.getInstance().time)
        }
    }
    fun getCurrentDay(): String? {
        val current: Locale = Locale.US
        return try {
            val formatter = SimpleDateFormat(format_FULLDAY, current)
            formatter.timeZone = TimeZone.getDefault()
            formatter.format(Calendar.getInstance().time)
        } catch (e: Exception) {
            SimpleDateFormat(format_FULLDAY, current)
                .format(Calendar.getInstance().time)
        }
    }

    fun getCurrentDateObject(): Date? {
        return Calendar.getInstance().time
    }

    fun getCurrentDateString(): String {
        val current: Locale = Locale.US
        return try {
            val formatter = SimpleDateFormat(format_YEAR_MONTH_DAY, current)
            formatter.timeZone = TimeZone.getDefault()
            formatter.format(Calendar.getInstance().time)
        } catch (e: Exception) {
            SimpleDateFormat(format_YEAR_MONTH_DAY, current)
                .format(Calendar.getInstance().time)
        }
    }

    fun getCurrentDateForScheme(schema: String?): String {
        val current: Locale = Locale.US
        return try {
            var formatter: SimpleDateFormat? = null
            formatter = if (schema != null) {
                SimpleDateFormat(schema, current)
            } else {
                SimpleDateFormat(format_FULLDAY_MONTH_DAY_YEAR, current)
            }
            formatter.timeZone = TimeZone.getDefault()
            formatter.format(Calendar.getInstance().time)
        } catch (e: Exception) {
            SimpleDateFormat(format_FULLDAY_MONTH_DAY_YEAR, current)
                .format(Calendar.getInstance().time)
        }
    }

    fun getStringDateByScheme(dateStr: String, schema: String? = null): String {
        val inputFormat = SimpleDateFormat(format_YEAR_MONTH_DAY, Locale.US)
        return try {
            val inputDate: Date = inputFormat.parse(dateStr) ?: Date()
            var formatter: SimpleDateFormat? = null
            formatter = if (schema != null) {
                SimpleDateFormat(schema, Locale.US)
            } else {
                SimpleDateFormat(format_DATE_TIME, Locale.US)
            }
            formatter.format(inputDate)
        } catch (e: Exception) {
            e.printStackTrace()
            dateStr
        }
    }

    fun getDateByScheme(dateStr: String?): Date? {
        val inputFormat = SimpleDateFormat(format_YEAR_MONTH_DAY, Locale.US)
        return try {
            inputFormat.parse(dateStr)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun isToday(dateString: String): Boolean {
        getStringDateByScheme(dateString, format_YEAR_MONTH_DAY)
        return getStringDateByScheme(dateString, format_YEAR_MONTH_DAY) == getCurrentDateForScheme(format_YEAR_MONTH_DAY)
    }

    /**
     * ADD - Helper methods with JodaTime
     */
    fun jodaDateTimeObject(dateString: String): DateTime? {
        val inputFormatter: DateTimeFormatter = DateTimeFormat.forPattern(format_YEAR_MONTH_DAY)
        val event = getStringDateByScheme(
            dateString,
            format_YEAR_MONTH_DAY
        )
        return inputFormatter.parseDateTime(event)
    }

    fun jodaLocalDateObject(dateString: String): LocalDate? {
        val inputFormatter: DateTimeFormatter = DateTimeFormat.forPattern(format_YEAR_MONTH_DAY)
        val event = getStringDateByScheme(
            dateString,
            format_YEAR_MONTH_DAY
        )
        return inputFormatter.parseLocalDate(event)
    }

    fun minutesDifference(dateStr: String?): Int {
        val MILLI_TO_MINUTE = 1000 * 60
        val formatter = SimpleDateFormat("yyyyy-MM-dd H:m:s")
        val currentDate = Date()
        try {
            formatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC))
            var date: Date = formatter.parse(dateStr)
            formatter.setTimeZone(TimeZone.getDefault())
            val newDate: String = formatter.format(date)
            date = formatter.parse(newDate)
            return (currentDate.getTime() - date.getTime()) as Int / MILLI_TO_MINUTE
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

    fun getDateToMillSeconds(dateStr: String?): Long {
        val formatter = SimpleDateFormat(format_DATE_MILITARY_TIME)
        try {
            formatter.setTimeZone(TimeZone.getTimeZone(TIMEZONE_UTC))
            val date: Date = formatter.parse(dateStr)
            return date.getTime()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }

}

