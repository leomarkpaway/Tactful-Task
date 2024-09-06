package com.leomarkpaway.todoapp.data.source.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.leomarkpaway.todoapp.data.source.local.dao.NotificationDao
import com.leomarkpaway.todoapp.data.source.local.dao.TaskDao
import com.leomarkpaway.todoapp.data.source.local.database.AppDatabase.Companion.VERSION_NUMBER
import com.leomarkpaway.todoapp.data.source.local.entity.Notification
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import com.leomarkpaway.todoapp.common.extension.createRoomDataBase

@Database(
    entities = [Task::class, Notification::class],
    version = VERSION_NUMBER,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun todoDao(): TaskDao
    abstract fun notifiedTaskDao(): NotificationDao

    companion object {
        const val VERSION_NUMBER = 1

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = context.createRoomDataBase<AppDatabase>("database_name")
                INSTANCE = instance
                instance
            }
        }
    }
}