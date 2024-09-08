package com.leomarkpaway.tactfultask.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.leomarkpaway.tactfultask.MyApp
import com.leomarkpaway.tactfultask.MyApp.Companion.CHANNEL_ID
import com.leomarkpaway.tactfultask.R
import com.leomarkpaway.tactfultask.common.enum.IntentAction
import com.leomarkpaway.tactfultask.common.extension.createNotification
import com.leomarkpaway.tactfultask.common.extension.createPendingDeepLink
import com.leomarkpaway.tactfultask.common.factory.createFactory
import com.leomarkpaway.tactfultask.common.util.getCurrentDateTimeMillis
import com.leomarkpaway.tactfultask.data.source.local.entity.Notification
import com.leomarkpaway.tactfultask.receiver.viewmodel.ReceiverViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Receiver : BroadcastReceiver() {

    private val viewModelFactory = createFactory { ReceiverViewModel(MyApp.appModule.repository) }
    private val viewModel: ReceiverViewModel = ViewModelProvider(ViewModelStore(), viewModelFactory)[ReceiverViewModel::class.java]
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private lateinit var notification: Notification
    override fun onReceive(context: Context?, intent: Intent?) {
        val taskId = intent?.extras?.getLong(TaskIdKey) ?: 0

        when(intent?.action) {
            IntentAction.NOTIFY_REMINDER_AND_SET_TASK_EXPIRED.name -> {
                createAndSaveNotification(context, taskId)
                viewModel.setTaskExpired(taskId)
            }
            IntentAction.NOTIFY_REMINDER.name -> {
                createAndSaveNotification(context, taskId)
            }
            IntentAction.SET_TASK_EXPIRED.name -> {
                viewModel.setTaskExpired(taskId)
            }
        }

    }

    private fun createAndSaveNotification(context: Context?, taskId: Long) {
        coroutineScope.launch {
            val notificationDate = getCurrentDateTimeMillis()
            val task = viewModel.getTaskById(taskId)
            val args = Bundle().apply { putLong("taskId", taskId) }

            if (task != null && context != null) {
                val pendingIntent =
                    context.createPendingDeepLink(R.navigation.nav_graph, R.id.taskDetailFragment, args)
                context.createNotification(
                    CHANNEL_ID,
                    taskId.toInt(),
                    NotificationTitle,
                    task.title,
                    pendingIntent
                )
                notification = Notification(task = task, dateMillis = notificationDate)
            }
            viewModel.saveNotification(notification)
        }
    }

    companion object {
        val TAG: String = Receiver::class.java.simpleName
        const val TaskIdKey = "task_id"
        const val NotificationTitle = "Today Task"
    }
}