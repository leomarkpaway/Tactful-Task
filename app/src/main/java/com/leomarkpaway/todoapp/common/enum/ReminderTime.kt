package com.leomarkpaway.todoapp.common.enum

enum class ReminderTime(val value: Long) {
    FIVE_MINS_EARLY(5),
    TEN_MINS_EARLY(10),
    FIFTEEN_MINS_EARLY(15),
    THIRTY_MINS_EARLY(30),
    ONE_HOUR_EARLY(60)
}