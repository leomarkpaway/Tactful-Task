package com.leomarkpaway.todoapp.di

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import com.leomarkpaway.todoapp.receiver.Receiver
import com.leomarkpaway.todoapp.common.enum.IntentAction
import com.leomarkpaway.todoapp.common.extension.getReminderMillis
import com.leomarkpaway.todoapp.common.extension.pendingIntentReceiver
import com.leomarkpaway.todoapp.data.source.local.entity.Task

interface Scheduler {
    suspend fun schedule(task: Task)
    suspend fun cancel(task: Task)
}

class SchedulerImpl(private val context: Context): Scheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    override suspend fun schedule(task: Task) {
        val taskId = task.id
        val selectedReminder = task.reminder
        if (selectedReminder == 0) {
            setAlarm(taskId, task.scheduleMillis, IntentAction.NOTIFY_REMINDER_AND_SET_TASK_EXPIRED.name)
        } else {
            val reminderSchedule = task.scheduleMillis.getReminderMillis(selectedReminder)
            // create and save notification
            setAlarm(taskId, reminderSchedule, IntentAction.NOTIFY_REMINDER.name)
            // set task to expired
            setAlarm(taskId, task.scheduleMillis, IntentAction.SET_TASK_EXPIRED.name)
        }
    }

    override suspend fun cancel(task: Task) {
        alarmManager.cancel(
            context.pendingIntentReceiver<Receiver>(
                requestCode = task.id.toInt(),
                flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE, {})
        )
    }

    private fun setAlarm(taskId: Long, schedule: Long, intentAction: String) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            schedule,
            context.pendingIntentReceiver<Receiver>(
                requestCode = taskId.toInt(),
                flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            ) { intent ->
                intent.putExtra(Receiver.TaskIdKey, taskId)
                intent.action = intentAction
            }
        )
    }
}
