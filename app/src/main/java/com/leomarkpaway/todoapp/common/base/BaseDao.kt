package com.leomarkpaway.todoapp.common.base

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(data: T): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    fun update(data: T)

    @Delete
    fun delete(vararg data: T)
}