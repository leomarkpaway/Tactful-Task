package com.leomarkpaway.todoapp.view.notification.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.common.enum.Pattern
import com.leomarkpaway.todoapp.common.extension.toMillis
import com.leomarkpaway.todoapp.data.source.local.entity.Notification
import com.leomarkpaway.todoapp.databinding.ItemNotificationBinding
import java.util.Calendar

class NotificationAdapter(
    private val itemList: ArrayList<Notification>,
    private val onItemClicked: (Long) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotifiedTaskHolder>() {
    private lateinit var context: Context

    inner class NotifiedTaskHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Notification) = with(binding) {
            val task = item.task
            val calendar: Calendar = Calendar.getInstance()
            tvTitle.text = task.title
            tvLabel.text = "-- --"
            tvDate.text = item.dateMillis.toMillis(calendar, Pattern.DATE.id)
            tvTime.text = item.dateMillis.toMillis(calendar, Pattern.TIME.id)
            root.setOnClickListener { onItemClicked(item.task.id) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifiedTaskHolder {
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemNotificationBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_notification,
            parent,
            false
        )
        return NotifiedTaskHolder(binding)
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: NotifiedTaskHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

}