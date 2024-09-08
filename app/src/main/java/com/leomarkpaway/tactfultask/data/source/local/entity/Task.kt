package com.leomarkpaway.tactfultask.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String? = "",
    val label: String = "none",
    @ColumnInfo("is_reminder_on") val isReminderOn: Boolean = false,
    val reminder: Int = 0,
    @ColumnInfo("schedule_millis") val scheduleMillis: Long = 0,
    val status: String = "none",
    @ColumnInfo("is_pinned") val isPinned: Boolean = false
)