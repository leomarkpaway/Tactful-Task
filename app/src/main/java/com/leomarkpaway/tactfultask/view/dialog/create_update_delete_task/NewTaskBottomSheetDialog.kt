package com.leomarkpaway.tactfultask.view.dialog.create_update_delete_task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.leomarkpaway.tactfultask.MyApp
import com.leomarkpaway.tactfultask.R
import com.leomarkpaway.tactfultask.common.enum.TaskStatus
import com.leomarkpaway.tactfultask.common.extension.showSoftKeyboard
import com.leomarkpaway.tactfultask.common.extension.showToastLong
import com.leomarkpaway.tactfultask.common.extension.toLocalDate
import com.leomarkpaway.tactfultask.common.util.InputValidatorUtil
import com.leomarkpaway.tactfultask.common.util.getCurrentDateMillis
import com.leomarkpaway.tactfultask.common.util.getDateIndicator
import com.leomarkpaway.tactfultask.common.util.viewModelFactory
import com.leomarkpaway.tactfultask.data.source.local.entity.Task
import com.leomarkpaway.tactfultask.databinding.DialogFragmentNewTaskBinding
import com.leomarkpaway.tactfultask.view.dialog.create_update_delete_task.viewmodel.DialogSharedViewModel
import com.leomarkpaway.tactfultask.view.dialog.schedule.SetScheduleBottomSheetDialog
import com.leomarkpaway.tactfultask.view.dialog.schedule.viewmodel.ScheduleSharedViewModel

class NewTaskBottomSheetDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogFragmentNewTaskBinding
    private val dialogSharedViewModel: DialogSharedViewModel by activityViewModels {
        viewModelFactory { DialogSharedViewModel(MyApp.appModule.repository) }
    }
    private val scheduleSharedViewModel: ScheduleSharedViewModel by activityViewModels {
        viewModelFactory { ScheduleSharedViewModel() }
    }
    private lateinit var title: InputValidatorUtil
    private var scheduleDialog: SetScheduleBottomSheetDialog? = null
    private var selectedDateMillis = 0L
    private var isReminderOn = false
    private var isDateValid = false
    private var selectedReminder = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentNewTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialogState()
        onSetScheduleAction()
        onSetLabelAction()
        onSaveAction()
        observerSelectedDateMillis()
        observerSelectedReminder()
        observeIsReminderOn()
        observeIsDateValid()
    }

    private fun initDialogState() {
        if (selectedDateMillis == 0L) scheduleSharedViewModel.updateSelectDateMillis(getCurrentDateMillis())
        binding.edtTaskName.requestFocus()
        requireContext().showSoftKeyboard(binding.edtTaskName)
    }

    private fun onSetScheduleAction() = with(binding) {
        btnSetReminder.setOnClickListener {
            scheduleDialog = SetScheduleBottomSheetDialog()
            scheduleDialog?.show(requireActivity().supportFragmentManager, "set_schedule")
        }
    }

    private fun onSetLabelAction() = with(binding) {
        btnLabel.setOnClickListener {
            // TODO implement set task label
        }
    }

    private fun onSaveAction() = with(binding) {
        btnSave.setOnClickListener {
            title = InputValidatorUtil(getString(R.string.warning_required), iplTaskName,).apply { setListener(edtTaskName) }

            val taskDescription = edtDesc.text.toString()
            val task = Task(
                title = title.getStringValue(),
                description = taskDescription,
                isReminderOn = isReminderOn,
                reminder = selectedReminder,
                scheduleMillis = selectedDateMillis,
                status = TaskStatus.PENDING.name
            )

            if (title.isValid() && isDateValid) {
                dialogSharedViewModel.addTodo(task)
                requireContext().showToastLong(R.string.new_task_add_successful)
                edtTaskName.setText("")
                edtDesc.setText("")
                dismiss()
            }

            if (!title.isValid()) {
                iplTaskName.helperText = getString(R.string.warning_required)
            }

        }
    }

    private fun setupDateIndicator(dueDateMillis: Long) = with(binding) {
        val scheduleDate = dueDateMillis.toLocalDate()
        val dateIndicator = getDateIndicator(scheduleDate)

        btnSetReminder.text = dateIndicator
        if (dateIndicator == getString(R.string.invalid)) {
            btnSetReminder.setTextColor(requireContext().getColor(R.color.red))
            dialogSharedViewModel.updateIsDateValid(false)
        } else {
            btnSetReminder.setTextColor(requireContext().getColor(R.color.smoked_oak_brown))
            dialogSharedViewModel.updateIsDateValid(true)
        }
    }

    private fun observerSelectedDateMillis() {
        scheduleSharedViewModel.selectDateMillis.observe(viewLifecycleOwner) { selectedDateMillis ->
            this.selectedDateMillis = selectedDateMillis
            if (selectedDateMillis != 0L) setupDateIndicator(selectedDateMillis)
        }
    }

    private fun observerSelectedReminder() {
        scheduleSharedViewModel.selectedReminder.observe(viewLifecycleOwner) { selectedReminder ->
            this.selectedReminder = selectedReminder
        }
    }

    private fun observeIsReminderOn() {
        scheduleSharedViewModel.isReminderOn.observe(viewLifecycleOwner) { isReminderOn ->
            this.isReminderOn = isReminderOn
        }
    }

    private fun observeIsDateValid() {
        dialogSharedViewModel.isDateValid.observe(viewLifecycleOwner) { isDateValid ->
            this.isDateValid = isDateValid
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scheduleDialog?.resetDialogState()
    }

    companion object {
        private val TAG = NewTaskBottomSheetDialog::class.java.simpleName
    }

}