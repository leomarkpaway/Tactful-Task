package com.leomarkpaway.tactfultask.data.source.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.leomarkpaway.tactfultask.data.source.local.entity.Notification
import com.leomarkpaway.tactfultask.common.base.BaseDao

@Dao
interface NotificationDao : BaseDao<Notification> {

    @Query("SELECT * FROM Notification WHERE id = :id")
    fun getByIdNotification(id: Long): LiveData<Notification>

    @Query("SELECT * FROM Notification ORDER BY date_millis DESC")
    fun getNotifications(): LiveData<List<Notification>>

    @Query("DELETE FROM notification")
    fun clearNotification()

}