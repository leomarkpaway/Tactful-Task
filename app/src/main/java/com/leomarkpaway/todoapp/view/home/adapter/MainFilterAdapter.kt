package com.leomarkpaway.todoapp.view.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.leomarkpaway.todoapp.view.home.adapter.model.HomeFilterModel
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.common.extension.gone
import com.leomarkpaway.todoapp.common.extension.toDateString
import com.leomarkpaway.todoapp.common.extension.visible
import com.leomarkpaway.todoapp.common.util.getCurrentDate
import com.leomarkpaway.todoapp.common.util.getDayDate
import com.leomarkpaway.todoapp.common.util.toShortDayName
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import com.leomarkpaway.todoapp.databinding.ItemDayBinding

class MainFilterAdapter(
    private val weeklyTaskList: List<Task>,
    private val selectedItem: Int,
    private val pairDateNameList: ArrayList<Pair<String, String>>,
    private val onItemClicked: (HomeFilterModel) -> Unit
) : RecyclerView.Adapter<MainFilterAdapter.TodoHolder>() {
    private lateinit var context: Context
    inner class TodoHolder(private val binding: ItemDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemPair: Pair<String, String>, position: Int) = with(binding) {
            val selectedFilter = HomeFilterModel(position, itemPair.first)
            setItemDay(this, itemPair, position)
            setItemClickListener(this, selectedFilter)
            setItemSelected(this, position)
            setCurrentDayIndicator(this, itemPair.first, position)
            setupItemCountByDate(binding, weeklyTaskList, itemPair.first, position)
        }
    }

    private fun setupItemCountByDate(
        binding: ItemDayBinding,
        weeklyTaskList: List<Task>,
        date: String,
        position: Int
    ) = with(binding) {
        val itemCount = weeklyTaskList.filter { it.scheduleMillis.toDateString() == date }.size
        if (itemCount != 0) {
            tvItemCount.text = itemCount.toString()
            cvItemCount.visible()
            if (selectedItem == position) {
                val smokedOakBrown = ContextCompat.getColor(context, R.color.smoked_oak_brown)
                cvItemCount.apply {
                    background.setTint(smokedOakBrown)
                    strokeColor = smokedOakBrown
                }
            }
        } else {
            cvItemCount.gone()
        }
    }

    private fun setItemSelected(binding: ItemDayBinding, position: Int) = with(binding) {
        val lightYellow = ContextCompat.getColor(context, R.color.light_yellow)
        val white =  ContextCompat.getColor(context, R.color.white)

        if (position == selectedItem) {
            tvDayShortName.setTextColor(lightYellow)
            tvDayDate.setTextColor(white)
            cvDay.apply {
                background.setTint(lightYellow)
                strokeColor = lightYellow
            }
        }
    }

    private fun setCurrentDayIndicator(binding: ItemDayBinding, dayDate: String, position: Int) = with(binding) {
        val smokedOakBrown = ContextCompat.getColor(context, R.color.smoked_oak_brown)
        val white =  ContextCompat.getColor(context, R.color.white)

        if (dayDate == getCurrentDate() && position != selectedItem) {
            tvDayDate.setTextColor(smokedOakBrown)
            cvDay.apply {
                background.setTint(white)
                strokeColor = smokedOakBrown
            }
        }
    }

    private fun setItemDay(binding: ItemDayBinding, itemPair: Pair<String, String>, position: Int) = with(binding) {
        val (date, dayName) = itemPair
        if (position == 0 || date.isEmpty()) {
            imgAll.visible()
            tvDayDate.gone()
            tvDayShortName.text = dayName
        } else {
            val dayShortName = dayName.uppercase().toShortDayName()
            tvDayDate.text = date.getDayDate()
            tvDayShortName.text = dayShortName
            imgAll.gone()
            tvDayDate.visible()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setItemClickListener (binding: ItemDayBinding, selectedFilter: HomeFilterModel) {
        binding.root.setOnClickListener {
            onItemClicked(selectedFilter)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoHolder {
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemDayBinding>(
            LayoutInflater.from(context),
            R.layout.item_day,
            parent,
            false
        )
        return TodoHolder(binding)
    }

    override fun onBindViewHolder(holder: TodoHolder, position: Int) {
        val item = pairDateNameList[position]
        holder.bind(item, position)
    }

    override fun getItemCount() = pairDateNameList.size

}