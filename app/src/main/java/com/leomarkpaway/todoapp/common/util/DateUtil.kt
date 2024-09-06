package com.leomarkpaway.todoapp.common.util

import com.leomarkpaway.todoapp.common.enum.Pattern
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

fun getCurrentWeekDateAndDayName(): ArrayList<Pair<String, String>> {
    val dateFormatter = DateTimeFormatter.ofPattern(Pattern.DATE.id)
    val weekDateAndDayNames = ArrayList<Pair<String, String>>()
    val currentWeekDates = getCurrentWeekDates()
    val startOfWeek = currentWeekDates[0]

    var weekIndex = 0L
    for (date in currentWeekDates) {
        val dateString = startOfWeek.plusDays(weekIndex).format(dateFormatter)
        val dayName = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        weekDateAndDayNames.add(Pair(dateString, dayName))
        weekIndex++
    }
    return weekDateAndDayNames
}

fun getCurrentWeekDates(): List<LocalDate> {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}

fun String.getDayDate() = this.split("/")[1]

fun String.toShortDayName(): String {
    val shortDayName = when (this) {
        "MONDAY" -> { "Mon" }
        "TUESDAY" -> { "Tues" }
        "WEDNESDAY" -> { "Wed" }
        "THURSDAY" -> { "Thur" }
        "FRIDAY" -> { "Fri" }
        "SATURDAY" -> { "Sat" }
        "SUNDAY" -> { "Sun" }
        else -> ""
    }
    return shortDayName
}

fun getCurrentDate() : String {
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern(Pattern.DATE.id)
    return currentDate.format(formatter)
}

fun getDateIndicator(date: LocalDate): String {
    val today = LocalDate.now()
    val currentWeek = getCurrentWeekDates()
    val nextWeek = currentWeek[0].plusWeeks(1)
    val nextMonth = today.plusMonths(1).withDayOfMonth(1).withDayOfMonth(1)
    val firstOfJanuary = today.withDayOfYear(1)
    val remainingMonths = ChronoUnit.MONTHS.between(firstOfJanuary, today.withDayOfMonth(1).minusMonths(1))
    val dateFormat = DateTimeFormatter.ofPattern(Pattern.MONTH_DAY_DATE.id)

    return when {
        date.isEqual(today) -> "Today"
        date.isEqual(today.plusDays(1)) -> "Tomorrow"
        date.isEqual(today.plusDays(2)) && !date.isEqual(currentWeek[5]) && !date.isEqual(currentWeek[6]) -> "2 days later"
        date.dayOfWeek == DayOfWeek.SATURDAY && date.isEqual(currentWeek[5]) && today.isBefore(date) -> "Saturday"
        date.dayOfWeek == DayOfWeek.SUNDAY && date.isEqual(currentWeek[6]) && today.isBefore(date) -> "Sunday"
        date.isBefore(nextWeek.plusWeeks(1)) && date.isAfter(today) -> {
            "Next ${date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }}"
        }
        date.isAfter(nextWeek.minusDays(1)) && date.isBefore(nextMonth.plusMonths(remainingMonths)) -> {
            "${date.format(dateFormat)}, ${ChronoUnit.DAYS.between(today, date)} days left"
        }
        date.year != today.year -> { "${date.format(dateFormat)}, ${date.year}" }
        else -> "${date.format(dateFormat)}, ${date.year}"
    }
}

fun isValidDate(date: LocalDate): Boolean {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)
    val startOfCurrentMonth = today.withDayOfMonth(1)
    val startOfCurrentYear = today.withDayOfYear(1)

    return when {
        date.isBefore(startOfCurrentYear) -> false // Date is from last year
        date.isBefore(startOfCurrentMonth) -> false // Date is from last month
        date.isEqual(yesterday) -> false // Date is yesterday
        else -> true
    }
}

fun Long.isDateTimeValid(): Boolean {
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    val currentDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(getCurrentDateTimeMillis()), ZoneId.systemDefault())
    return !dateTime.isBefore(currentDateTime)
}

fun String.dateStringToLocalDate(datePattern: String) : LocalDate {
    val dateFormatter = DateTimeFormatter.ofPattern(datePattern)
    return LocalDate.parse(this, dateFormatter) as LocalDate
}

// get exact local date with time
fun getCurrentDateTimeMillis(): Long {
    return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

// get exact local date only
fun getCurrentDateMillis(): Long {
    return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun getStartAndEndOfDay(): Pair<Long, Long> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfDay = calendar.timeInMillis

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfDay = calendar.timeInMillis

    return Pair(startOfDay, endOfDay)
}

fun getStartAndEndOfDay(date: String): Pair<Long, Long> {
    val dateFormat = SimpleDateFormat(Pattern.DATE.id, Locale.getDefault())
    val parsedDate = dateFormat.parse(date)

    val calendar = Calendar.getInstance()
    calendar.time = parsedDate

    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfDay = calendar.timeInMillis

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfDay = calendar.timeInMillis

    return Pair(startOfDay, endOfDay)
}

fun getStartAndEndOfCurrentWeek(): Pair<Long, Long> {
    val calendar = Calendar.getInstance(Locale.getDefault())

    calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val startOfWeek = calendar.timeInMillis

    calendar.add(Calendar.DAY_OF_WEEK, 6)
    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val endOfWeek = calendar.timeInMillis

    return Pair(startOfWeek, endOfWeek)
}

fun LocalTime.to12HourFormat(): String {
    val formatter = DateTimeFormatter.ofPattern(Pattern.TIME.id)
    return this.format(formatter)
}

fun Long.isDateOverdue(): Boolean {
    return this < getCurrentDateTimeMillis()
}

fun getPositionCurrentDatePosition() : Int {
    val currentDate = LocalDate.now()
    val dayOfWeek = currentDate.dayOfWeek
    return dayOfWeek.value - 1
}