package com.leomarkpaway.todoapp.common.util

import java.util.Calendar

fun isTaskUpdatedValue(oldTask: Task, newTask: Task) : Boolean {
    val propertiesToCompare = listOf(
        oldTask.title to newTask.title,
        oldTask.description to newTask.description,
        oldTask.label to newTask.label,
        oldTask.isReminderOn to newTask.isReminderOn,
        oldTask.scheduleMillis to newTask.scheduleMillis,
        oldTask.status to newTask.status,
        oldTask.reminder to newTask.reminder,
        oldTask.isPinned to newTask.isPinned,
    )

    for ((prop1, prop2) in propertiesToCompare) { if (prop1 != prop2) return true }
    return false
}

fun isScheduleValid(oldSchedule: Long, newSchedule: Long): Boolean {
    val currentDate = System.currentTimeMillis()
    if (oldSchedule == newSchedule) return false
    return (newSchedule > currentDate)
}

fun isDateIsSame(oldSchedule: Long, newSchedule: Long) : Boolean{
    val currentDate = stripTimeFromMillis(oldSchedule)
    val newScheduleDate = stripTimeFromMillis(newSchedule)
    return newScheduleDate > currentDate
}

fun stripTimeFromMillis(millis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}