package com.leomarkpaway.tactfultask.di

import android.content.Context
import com.leomarkpaway.tactfultask.data.repository.Repository
import com.leomarkpaway.tactfultask.data.repository.RepositoryImpl
import com.leomarkpaway.tactfultask.data.source.local.database.AppDatabase

interface AppModule {
    val repository: Repository
    val scheduler: Scheduler
}
class AppModuleImpl(appContext: Context) : AppModule {

    private val database = AppDatabase.getDatabase(appContext)
    override val scheduler: Scheduler = SchedulerImpl(appContext)
    override val repository: Repository = RepositoryImpl(database, scheduler)

}