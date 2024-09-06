package com.leomarkpaway.todoapp.view.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.common.enum.Pattern
import com.leomarkpaway.todoapp.common.enum.TaskStatus
import com.leomarkpaway.todoapp.common.extension.setImage
import com.leomarkpaway.todoapp.common.extension.setRippleEffect
import com.leomarkpaway.todoapp.common.extension.toLocalDate
import com.leomarkpaway.todoapp.common.extension.toMillis
import com.leomarkpaway.todoapp.common.util.getDateIndicator
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import com.leomarkpaway.todoapp.databinding.ItemTodayTaskBinding
import java.util.Calendar
import java.util.Locale

class TaskAdapter(
    private var itemList: List<Task>? = emptyList(),
    private val onItemClicked: (Task) -> Unit,
    private val onItemDelete: (Task) -> Unit? = {},
    private val onItemPin: (Task) -> Unit? = {},
    private val onClickDone: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TodoHolder>() {

    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private lateinit var context: Context

    inner class TodoHolder(private val binding: ItemTodayTaskBinding) : RecyclerView.ViewHolder(binding.root) {
       @SuppressLint("NotifyDataSetChanged")
       fun bind(item: Task) = with(binding) {
           val reminderOption = context.resources.getStringArray(R.array.reminder_options)
           val title = item.title.capitalize(Locale.getDefault())
           val date = getDateIndicator(item.scheduleMillis.toLocalDate())
           val time = item.scheduleMillis.toMillis(calendar, Pattern.TIME.id).lowercase(Locale.getDefault())
           val timeAndReminder = "$time, ${reminderOption[item.reminder]}"

           tvTitle.text = title
           tvDate.text = date
           tvTime.text = if (item.isReminderOn) timeAndReminder else "Reminder: none"
           imgPin.setImage(context, if (item.isPinned) R.drawable.ic_pin else R.drawable.ic_unpin)

           when(item.status) {
               TaskStatus.PENDING.name -> {
                   checkBox.isChecked = false
                   imgPin.setRippleEffect()
                   imgDelete.setRippleEffect()
                   if (item.scheduleMillis < System.currentTimeMillis() && item.isReminderOn) tvDate.setTextColor(
                       ContextCompat.getColor(context, R.color.red)
                   )
               }
               TaskStatus.DONE.name -> {
                   val gray = ContextCompat.getColor(context, R.color.stonewall_grey)
                   imgPin.colorFilter = PorterDuffColorFilter(gray, PorterDuff.Mode.SRC_IN)
                   imgDelete.colorFilter = PorterDuffColorFilter(gray, PorterDuff.Mode.SRC_IN)
                   checkBox.isChecked = true
                   tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
               }
               TaskStatus.EXPIRED.name -> {
                   tvDate.setTextColor(ContextCompat.getColor(context, R.color.red))
               }
           }

           root.setOnClickListener { onItemClicked(item) }
           imgDelete.setOnClickListener {
               if (TaskStatus.DONE.name != item.status) onItemDelete(item)
               notifyDataSetChanged()
           }
           imgPin.setOnClickListener {
               if (TaskStatus.DONE.name != item.status) onItemPin(item)
               notifyDataSetChanged()
           }
           checkBox.setOnClickListener {
               onClickDone(item)
               checkBox.isChecked = true
               notifyDataSetChanged()
           }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoHolder {
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemTodayTaskBinding>(LayoutInflater.from(context), R.layout.item_today_task, parent, false)
        return TodoHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoHolder, position: Int) {
        val item = itemList?.get(position)
        item?.let { holder.bind(it) }
    }

    override fun getItemCount() = itemList?.size ?: 0

}