package com.leomarkpaway.todoapp.view.dialog.create_update_delete_task

import androidx.fragment.app.viewModels
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.MyApp
import com.leomarkpaway.todoapp.common.base.BaseDialogFragment
import com.leomarkpaway.todoapp.common.enum.Pattern
import com.leomarkpaway.todoapp.common.extension.toMillis
import com.leomarkpaway.todoapp.common.util.viewModelFactory
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import com.leomarkpaway.todoapp.databinding.DialogFragmentDeleteTaskBinding
import com.leomarkpaway.todoapp.view.dialog.create_update_delete_task.viewmodel.DialogSharedViewModel
import java.util.Calendar

class DeleteTaskDialog(private val task: Task, private val onDelete: (() -> Unit)? = null) :
    BaseDialogFragment<DialogSharedViewModel, DialogFragmentDeleteTaskBinding>() {
    override val viewModel: DialogSharedViewModel by viewModels {
        viewModelFactory { DialogSharedViewModel(MyApp.appModule.repository) }
    }
    override val layout: Int = R.layout.dialog_fragment_delete_task

    override fun initViews() {
        super.initViews()
        viewModel.updateSelectedItem(task)
        onClickDelete()
        onCLickCancel()
    }

    override fun subscribe() {
        super.subscribe()
        observeSelectedItem()
    }

    private fun observeSelectedItem() {
        viewModel.selectedTask.observe(this) { item ->
            if (item != null) setupDialogContent(item)
        }
    }

    private fun setupDialogContent(task: Task) = with(binding.tvSelectedItem) {
        val calendar: Calendar = Calendar.getInstance()
        val time = task.scheduleMillis.toMillis(calendar, Pattern.TIME.id)
        val date = task.scheduleMillis.toMillis(calendar, Pattern.DATE.id)
        text = getString(R.string.selected_item_to_delete, task.title, time, date)
    }

    private fun onClickDelete() = with(binding.btnDelete) {
        setOnClickListener {
            viewModel.deleteTodo()
            onDelete?.invoke()
            dismiss()
        }
    }

    private fun onCLickCancel() = with(binding.btnCancel) {
        setOnClickListener { dismiss() }
    }

}