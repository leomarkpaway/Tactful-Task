package com.leomarkpaway.todoapp.data.source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import com.leomarkpaway.todoapp.common.base.BaseDao

@Dao
interface TaskDao : BaseDao<Task> {

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM task ORDER BY schedule_millis ASC")
    fun getAllDataSortByDate(): LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE schedule_millis BETWEEN :startOfDay AND :endOfDay ORDER BY schedule_millis ASC")
    fun getTasksByRangeDate(startOfDay: Long, endOfDay: Long): LiveData<List<Task>>

    @Query("SELECT schedule_millis FROM task WHERE id = :id")
    fun getTaskScheduleById(id: Long) : Long

    @Query("SELECT status FROM task WHERE id = :id")
    fun getTaskStatusById(id: Long) : String

    @Query("SELECT reminder FROM task WHERE id = :id")
    fun getTaskReminderById(id: Long) : Int

}