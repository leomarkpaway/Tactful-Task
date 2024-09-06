package com.leomarkpaway.todoapp.common.extension

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Creates an instance of a Room Database using the specified [databaseName].
 *
 * @param T The type of the Room Database, must be a subclass of [RoomDatabase].
 * @param databaseName The name of the Room Database.
 * @return An instance of the specified Room Database.
 *
 * @throws IllegalStateException if the Room Database cannot be instantiated.
 *
 * Usage example:
 * ```
 * val myDatabase = createRoomDataBase<MyDatabase>("my_database_name")
 * ```
 */
inline fun <reified T : RoomDatabase> Context.createRoomDataBase(databaseName: String) =
    Room.databaseBuilder(
        this.applicationContext,
        T::class.java,
        databaseName
    ).build()
