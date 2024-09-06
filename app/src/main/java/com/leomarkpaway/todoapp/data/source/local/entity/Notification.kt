package com.leomarkpaway.todoapp.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Notification(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("notification_id") val notificationId: Long = 0,
    @Embedded val task: Task,
    @ColumnInfo("date_millis") val dateMillis: Long
)