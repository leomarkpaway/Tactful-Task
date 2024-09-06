package com.leomarkpaway.todoapp

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.leomarkpaway.todoapp.common.extension.createNotificationChannel
import com.leomarkpaway.todoapp.di.AppModule
import com.leomarkpaway.todoapp.di.AppModuleImpl
import timber.log.Timber

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupTimberLogging()
        createNotification()
        appModule = AppModuleImpl(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    private fun createNotification() {
        this.createNotificationChannel(CHANNEL_ID, CHANNEL_NAME)
    }

    private fun setupTimberLogging() {
        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                super.log(priority, "Timber_$tag", message, t)
            }

            override fun createStackElementTag(element: StackTraceElement): String {
                return String.format(
                    "%s:%s",
                    super.createStackElementTag(element),
                    element.methodName
                )
            }
        })
    }

    companion object {
        lateinit var appModule: AppModule
        const val CHANNEL_ID = "TactfulTasks"
        const val CHANNEL_NAME = "Today task"
    }

}