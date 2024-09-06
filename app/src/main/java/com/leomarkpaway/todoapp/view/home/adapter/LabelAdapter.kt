package com.leomarkpaway.todoapp.view.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.common.extension.setImage
import com.leomarkpaway.todoapp.databinding.ItemTaskLabelBinding

class LabelAdapter(private val label: String, private val onClickHideTask: () -> Unit) :
    RecyclerView.Adapter<LabelAdapter.LabelViewHolder>() {
    private lateinit var context: Context
    private var isVisibleTaskList: Boolean = true

    inner class LabelViewHolder(val binding: ItemTaskLabelBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind() = with(binding) {
            tvTaskLabel.text = label
            imgHideTasks.setOnClickListener {
                isVisibleTaskList = !isVisibleTaskList
                onClickHideTask.invoke()
                imgHideTasks.setImage(
                    context,
                    if (isVisibleTaskList) R.drawable.ic_arrow_down_hide_list else R.drawable.ic_arrow_up_show_list
                )
                notifyDataSetChanged()
            }
        }
    }

    fun updateVisibilityIndicator(isVisible: Boolean) {
        isVisibleTaskList = isVisible
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemTaskLabelBinding>(
            LayoutInflater.from(context),
            R.layout.item_task_label,
            parent,
            false
        )
        return LabelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = 1
}