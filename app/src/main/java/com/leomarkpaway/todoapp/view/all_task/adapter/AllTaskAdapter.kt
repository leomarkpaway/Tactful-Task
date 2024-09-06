package com.leomarkpaway.todoapp.view.all_task.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.common.enum.TaskStatus
import com.leomarkpaway.todoapp.common.extension.gone
import com.leomarkpaway.todoapp.common.extension.setImage
import com.leomarkpaway.todoapp.common.extension.toLocalDate
import com.leomarkpaway.todoapp.common.extension.toTimeString
import com.leomarkpaway.todoapp.common.extension.visible
import com.leomarkpaway.todoapp.common.util.PopupMenu
import com.leomarkpaway.todoapp.common.util.getDateIndicator
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import com.leomarkpaway.todoapp.databinding.ItemAllTaskBinding
import java.util.Calendar
import java.util.Locale

class AllTaskAdapter(
    private val itemList: ArrayList<Task>,
    private val onItemClicked: (Task) -> Unit,
    private val onItemShowFullDetail: (Long) -> Unit,
    private val onItemPin: (Task) -> Unit,
    private val onItemUpdate: (Task) -> Unit,
    private val onItemDelete: (Task) -> Unit,
) : RecyclerView.Adapter<AllTaskAdapter.TodoHolder>(), Filterable {

    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private val itemListHolder = ArrayList<Task>(itemList)
    private lateinit var context: Context

    inner class TodoHolder(private val binding: ItemAllTaskBinding) : RecyclerView.ViewHolder(binding.root) {
       @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
       fun bind(item: Task) = with(binding) {
           val dateMillis = item.scheduleMillis
           val dateIndicator = getDateIndicator(dateMillis.toLocalDate())
           val time = if (item.isReminderOn) dateMillis.toTimeString(calendar).lowercase(Locale.getDefault()) else ""
           val popupMenuAction = PopupMenu(context, imgMiniMenu, R.menu.popup_menu_action)
           popupMenuAction.setMenuTitle(R.id.menu_pin, if (item.isPinned) "Unpin Task" else "Pin Task")
           popupMenuAction.setMenuIcon(R.id.menu_pin, if (item.isPinned) R.drawable.ic_pin else R.drawable.ic_unpin)

           when(item.status) {
               TaskStatus.PENDING.name -> {
                   imgStatus.setImage(context, R.drawable.ic_pending_task)
               }
               TaskStatus.DONE.name -> {
                   imgStatus.setImage(context, R.drawable.ic_completed_task)
                   tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
               }
               TaskStatus.EXPIRED.name -> {
                   imgStatus.setImage(context, R.drawable.ic_overdue_task)
                   tvDateTime.setTextColor(ContextCompat.getColor(context, R.color.red))
               }
           }
           tvDateTime.text = "$dateIndicator $time"
           imgPinnedIndicator.apply { if (item.isPinned) visible() else gone() }
           tvTitle.text = item.title.capitalize(Locale.getDefault())
           imgMiniMenu.setOnClickListener {
               popupMenuAction.setOnMenuItemClickListener { menu ->
                   when (menu.itemId) {
                       R.id.menu_show_details -> onItemShowFullDetail(item.id)
                       R.id.menu_pin -> {
                           menu.title = if (!item.isPinned) "Unpin Task" else "Pin Task"
                           menu.setIcon(if (!item.isPinned) R.drawable.ic_pin else R.drawable.ic_unpin)
                           onItemPin(item)
                           notifyDataSetChanged()
                       }
                       R.id.menu_update -> onItemUpdate(item)
                       R.id.menu_delete -> {
                           onItemDelete(item)
                           notifyDataSetChanged()
                       }
                   }
               }
               popupMenuAction.show()
           }
           root.setOnClickListener { onItemClicked(item) }
       }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoHolder {
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemAllTaskBinding>(LayoutInflater.from(context), R.layout.item_all_task, parent, false)
        return TodoHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount() = itemList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                val filteredList = mutableListOf<Task>()
                val results = FilterResults()
                if (constraint.isEmpty()) {
                    filteredList.addAll(itemListHolder)
                } else {
                    for (item in itemListHolder) {
                        if (item.title.contains(filterPattern, true)) filteredList.add(item)
                    }
                }
                results.values = filteredList
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                itemList.clear()
                @Suppress("UNCHECKED_CAST")
                itemList.addAll(results.values as ArrayList<Task>)
                notifyDataSetChanged()
            }
        }
    }

}