package com.leomarkpaway.todoapp.common.extension

import com.leomarkpaway.todoapp.common.enum.Pattern
import com.leomarkpaway.todoapp.common.enum.ReminderTime.FIFTEEN_MINS_EARLY
import com.leomarkpaway.todoapp.common.enum.ReminderTime.FIVE_MINS_EARLY
import com.leomarkpaway.todoapp.common.enum.ReminderTime.ONE_HOUR_EARLY
import com.leomarkpaway.todoapp.common.enum.ReminderTime.TEN_MINS_EARLY
import com.leomarkpaway.todoapp.common.enum.ReminderTime.THIRTY_MINS_EARLY
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar
import java.util.Locale

fun Long.toMillis(calendar: Calendar, pattern: String): String {
    calendar.timeInMillis = this
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(calendar.time)
}

fun Long.toDateTimeString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val sdf = SimpleDateFormat("${ Pattern.DATE.id } - ${Pattern.TIME.id}", Locale.getDefault())
    return sdf.format(calendar.time)
}

fun Long.toDateString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val sdf = SimpleDateFormat(Pattern.DATE.id, Locale.getDefault())
    return sdf.format(calendar.time)
}

fun Long.toTimeString(calendar: Calendar): String {
    calendar.timeInMillis = this
    val sdf = SimpleDateFormat(Pattern.TIME.id, Locale.getDefault())
    return sdf.format(calendar.time)
}

fun Long.toLocalDate(): LocalDate {
    val instant = Instant.ofEpochMilli(this)
    return instant.atZone(ZoneId.systemDefault()).toLocalDate()
}

fun Long.toLocalTime(): LocalTime {
    val instant = Instant.ofEpochMilli(this)
    return instant.atZone(ZoneId.systemDefault()).toLocalTime()
}

fun Long.toLocalDateTime(): LocalDateTime {
    val instant = Instant.ofEpochMilli(this)
    val zoneId = ZoneId.systemDefault()
    return LocalDateTime.ofInstant(instant, zoneId)
}

fun LocalDate.toMillis(): Long {
    val localDateTime = this.atTime(LocalTime.MIDNIGHT)
    val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
    return zonedDateTime.toInstant().toEpochMilli()
}

fun LocalTime.toMillis(): Long {
    val localDateTime = LocalDateTime.of(LocalDate.now(), this)
    val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
    val millisSinceEpoch = zonedDateTime.toInstant().toEpochMilli()
    val midnight = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    return millisSinceEpoch - midnight
}

fun LocalDateTime.toMillis(): Long {
    val zoneId = ZoneId.systemDefault()
    return this.atZone(zoneId).toInstant().toEpochMilli()
}

fun ZonedDateTime.toMillis(): Long {
    return this.toInstant().toEpochMilli()
}

fun Long.toZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
}

fun Long.getReminderMillis(selectedReminder: Int): Long {
    val dateTime = this.toZonedDateTime()
    return when (selectedReminder) {
        1 -> dateTime.minusMinutes(FIVE_MINS_EARLY.value).toMillis()
        2 -> dateTime.minusMinutes(TEN_MINS_EARLY.value).toMillis()
        3 -> dateTime.minusMinutes(FIFTEEN_MINS_EARLY.value).toMillis()
        4 -> dateTime.minusMinutes(THIRTY_MINS_EARLY.value).toMillis()
        5 -> dateTime.minusMinutes(ONE_HOUR_EARLY.value).toMillis()
        else -> dateTime.toMillis()
    }
}

fun Long.convertMillisTo12HourFormat(): Pair<Int, Int> {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = this@convertMillisTo12HourFormat
    }
    var hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    if (hour == 0) {
        hour = 12
    }
    return Pair(hour, minute)
}