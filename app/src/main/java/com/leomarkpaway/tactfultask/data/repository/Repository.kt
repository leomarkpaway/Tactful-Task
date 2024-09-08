package com.leomarkpaway.tactfultask.data.repository

import androidx.lifecycle.LiveData
import com.leomarkpaway.tactfultask.data.source.local.database.AppDatabase
import com.leomarkpaway.tactfultask.data.source.local.entity.Notification
import com.leomarkpaway.tactfultask.data.source.local.entity.Task
import com.leomarkpaway.tactfultask.common.enum.TaskStatus
import com.leomarkpaway.tactfultask.common.extension.getReminderMillis
import com.leomarkpaway.tactfultask.common.extension.toDateTimeString
import com.leomarkpaway.tactfultask.common.util.getStartAndEndOfCurrentWeek
import com.leomarkpaway.tactfultask.common.util.getStartAndEndOfDay
import com.leomarkpaway.tactfultask.common.util.isScheduleValid
import com.leomarkpaway.tactfultask.di.Scheduler
import timber.log.Timber

interface Repository {
    suspend fun addTask(task: Task)
    suspend fun getTask(id: Long) : Task?
    suspend fun getAllTask() : LiveData<List<Task>>
    suspend fun getTasksByDate(date: String) : LiveData<List<Task>>
    suspend fun getWeeklyTasks() : LiveData<List<Task>>
    suspend fun deleteTask(task: Task)
    suspend fun updateTask(newTask: Task)
    suspend fun setTaskExpired(task: Task)
    suspend fun updateTaskPin(task: Task)
    suspend fun markAsDoneTask(task: Task)
    suspend fun saveNotification(notifiedTask: Notification)
    suspend fun getNotification() : LiveData<List<Notification>>
    suspend fun clearNotification()
}

class RepositoryImpl(
    private val appDatabase: AppDatabase,
    private val scheduler: Scheduler
) : Repository {
    override suspend fun addTask(task: Task) {
        val taskId = appDatabase.todoDao().insert(task)
        val taskCopy = task.copy(id = taskId)
        if (taskCopy.isReminderOn) scheduler.schedule(taskCopy)
    }

    override suspend fun getTask(id: Long) : Task? {
        return appDatabase.todoDao().getTaskById(id)
    }

    override suspend fun getAllTask(): LiveData<List<Task>> {
        return appDatabase.todoDao().getAllDataSortByDate()
    }

    override suspend fun getTasksByDate(date: String): LiveData<List<Task>> {
        val (dayStart, dayEnd) = getStartAndEndOfDay(date)
        return appDatabase.todoDao().getTasksByRangeDate(dayStart, dayEnd)
    }

    override suspend fun getWeeklyTasks() : LiveData<List<Task>> {
        val (start, end) = getStartAndEndOfCurrentWeek()
        return appDatabase.todoDao().getTasksByRangeDate(start, end)
    }

    override suspend fun deleteTask(task: Task) {
        appDatabase.todoDao().delete(task)
        scheduler.cancel(task)
    }

    override suspend fun updateTask(newTask: Task) {
        val oldStatus = appDatabase.todoDao().getTaskStatusById(newTask.id)
        val oldReminder = appDatabase.todoDao().getTaskReminderById(newTask.id)
        val oldSchedule = appDatabase.todoDao().getTaskScheduleById(newTask.id)
            .getReminderMillis(oldReminder)
        val newReminder = newTask.reminder
        val newSchedule = newTask.scheduleMillis.getReminderMillis(newReminder)

        if (isScheduleValid(oldSchedule, newSchedule) && newTask.isReminderOn) {
            scheduler.cancel(newTask)
            scheduler.schedule(newTask)
            Timber.d("Re-scheduler alarm")
        }
        val updatedTask =
            if (oldStatus == TaskStatus.EXPIRED.name) newTask.copy(status = TaskStatus.PENDING.name) else newTask
        appDatabase.todoDao().update(updatedTask)
        Timber.d(
            "status %s, schedule %s",
            updatedTask.status,
            updatedTask.scheduleMillis.toDateTimeString()
        )
    }

    override suspend fun setTaskExpired(task: Task) {
        appDatabase.todoDao().update(task)
    }

    override suspend fun updateTaskPin(task: Task) {
        appDatabase.todoDao().update(task)
    }

    override suspend fun markAsDoneTask(task: Task) {
        appDatabase.todoDao().update(task)
    }

    override suspend fun saveNotification(notifiedTask: Notification) {
        appDatabase.notifiedTaskDao().insert(notifiedTask)
    }

    override suspend fun getNotification(): LiveData<List<Notification>> {
        return appDatabase.notifiedTaskDao().getNotifications()
    }

    override suspend fun clearNotification() {
        appDatabase.notifiedTaskDao().clearNotification()
    }

}