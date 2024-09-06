package com.leomarkpaway.todoapp.view.dialog.create_update_delete_task

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.leomarkpaway.todoapp.MyApp
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.common.extension.convertMillisTo12HourFormat
import com.leomarkpaway.todoapp.common.extension.setImage
import com.leomarkpaway.todoapp.common.extension.showToastLong
import com.leomarkpaway.todoapp.common.extension.toLocalDate
import com.leomarkpaway.todoapp.common.extension.toLocalTime
import com.leomarkpaway.todoapp.common.extension.toMillis
import com.leomarkpaway.todoapp.common.util.InputValidatorUtil
import com.leomarkpaway.todoapp.common.util.getDateIndicator
import com.leomarkpaway.todoapp.common.util.to12HourFormat
import com.leomarkpaway.todoapp.common.util.viewModelFactory
import com.leomarkpaway.todoapp.data.source.local.entity.Task
import com.leomarkpaway.todoapp.databinding.DialogFragmentViewUpdateTaskBinding
import com.leomarkpaway.todoapp.view.dialog.create_update_delete_task.viewmodel.DialogSharedViewModel
import com.leomarkpaway.todoapp.view.dialog.schedule.SetReminderDialog
import com.leomarkpaway.todoapp.view.dialog.schedule.viewmodel.ScheduleSharedViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class UpdateTaskBottomSheetDialog(private val selectedTask: Task) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogFragmentViewUpdateTaskBinding
    private val dialogSharedViewModel: DialogSharedViewModel by activityViewModels {
        viewModelFactory { DialogSharedViewModel(MyApp.appModule.repository) }
    }
    private val scheduleSharedViewModel: ScheduleSharedViewModel by activityViewModels {
        viewModelFactory { ScheduleSharedViewModel() }
    }
    private lateinit var selectedTaskHolder: Task
    private lateinit var title: InputValidatorUtil
    private var tempDateTime = ZonedDateTime.now()
    private var tempDate = LocalDate.now()
    private var tempTime = LocalTime.now()
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var timePicker: MaterialTimePicker
    private var isDateValid = false
    private var isReminderOn = false
    private lateinit var reminderOption: Array<String>
    private var selectedReminder = 0
    private var isPinned = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialogSharedViewModel.updateSelectedItem(selectedTask)
        binding = DialogFragmentViewUpdateTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onSetDateAction()
        onSetTimeAction()
        onSetReminderAction()
        onClickFullDetail()
        setupBottomSheet()
        onPinAction()
        observeSelectedTask()
        observeSelectedDateMillis()
        observeIsDateValid()
        observeIsReminderOn()
        observerSelectedReminder()
        observeIsTaskPinned()
    }

    private fun onSetDateAction() = with(binding) {
        val oneDayMillis = 86400000
            datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select a date")
            .setSelection(selectedTask.scheduleMillis + oneDayMillis)
            .build()
        llDate.setOnClickListener { showDatePicker() }
        conDate.setOnClickListener { showDatePicker() }
        imgArrowCrossDate.setOnClickListener { showDatePicker() }
        datePicker.addOnPositiveButtonClickListener {
            tempDate =
                datePicker.selection?.toLocalDate() ?: selectedTaskHolder.scheduleMillis.toLocalDate()
            scheduleSharedViewModel.updateSelectReminder(selectedReminder)
            setDateUiValue(tempDate.toMillis())
        }
    }

    private fun onSetTimeAction() = with(binding) {
        val (hour, minute) = selectedTask.scheduleMillis.convertMillisTo12HourFormat()
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Select Time")
            .build()

        llTime.setOnClickListener { showTimePicker() }
        conTime.setOnClickListener { showTimePicker() }
        imgArrowCrossTime.setOnClickListener {
            if (!isReminderOn) {
                showTimePicker()
            } else {
                scheduleSharedViewModel.updateIsReminderOn(false)
            }
        }
        timePicker.addOnPositiveButtonClickListener {
            tempTime = LocalTime.of(timePicker.hour, timePicker.minute)
            scheduleSharedViewModel.updateSelectReminder(selectedReminder)
            scheduleSharedViewModel.updateIsReminderOn(true)
        }
    }

    private fun onSetReminderAction() = with(binding) {
        conReminder.setOnClickListener { showDialogSetReminder() }
        llReminder.setOnClickListener { showDialogSetReminder() }
        imgArrowCrossReminder.setOnClickListener { showDialogSetReminder() }
    }

    private fun showDatePicker() {
        datePicker.show(requireActivity().supportFragmentManager, "set_date")
    }

    private fun showTimePicker() {
        timePicker.show(requireActivity().supportFragmentManager, "set_time")
    }

    private fun showDialogSetReminder() {
        SetReminderDialog(
            context = requireContext(),
            defaultReminder = selectedReminder,
            onNegativeAction = {},
            onReminderSelected = { selectedReminder ->
                scheduleSharedViewModel.updateSelectReminder(selectedReminder)
            },
        ).show()
    }

    private fun submitChanges() = with(binding) {
        title = InputValidatorUtil(getString(R.string.warning_required), iplTaskName,).apply { setListener(edtTaskName) }
        tempDateTime = ZonedDateTime.of(tempDate, tempTime, ZoneId.systemDefault())
        if (!title.isValid()) { iplTaskName.helperText = getString(R.string.warning_required) }

        if (title.isValid() && isDateValid) {
            val updatedTask = selectedTaskHolder.copy(
                title = title.getStringValue(),
                description = edtDesc.text.toString(),
                scheduleMillis = tempDateTime.toMillis(),
                isReminderOn = isReminderOn,
                reminder = selectedReminder,
                status = selectedTask.status
            )
            dialogSharedViewModel.updateTodo(updatedTask)
            dismiss()
        } else {
            requireContext().showToastLong("Invalid Input")
        }
    }

    private fun observeSelectedTask() = with(binding) {
        lifecycleScope.launch {
            dialogSharedViewModel.selectedTask.observe(viewLifecycleOwner) { task ->
                selectedTaskHolder = task
                setTitleAndDescriptionUiValue(task.title, task.description ?: "")
                scheduleSharedViewModel.updateSelectDateMillis(task.scheduleMillis)
                scheduleSharedViewModel.updateSelectReminder(task.reminder)
                scheduleSharedViewModel.updateIsReminderOn(task.isReminderOn)
                dialogSharedViewModel.updateIsPinned(task.isPinned)
            }
        }
    }

    private fun observeSelectedDateMillis() {
        scheduleSharedViewModel.selectDateMillis.observe(viewLifecycleOwner) { selectedDateMillis ->
            tempDate = selectedDateMillis.toLocalDate()
            tempTime = selectedDateMillis.toLocalTime()
            tempDateTime = ZonedDateTime.of(tempDate, tempTime, ZoneId.systemDefault())
            setDateUiValue(selectedDateMillis)
        }
    }

    private fun observeIsDateValid() {
        dialogSharedViewModel.isDateValid.observe(viewLifecycleOwner) { isDateValid ->
            this.isDateValid = isDateValid
            binding.tvDate.setTextColor(
                if (!isDateValid) ContextCompat.getColor(requireContext(), R.color.red)
                else ContextCompat.getColor(requireContext(), R.color.black_op_77)
            )
        }
    }

    private fun observeIsReminderOn() {
        scheduleSharedViewModel.isReminderOn.observe(viewLifecycleOwner) { isReminderOn ->
            this@UpdateTaskBottomSheetDialog.isReminderOn = isReminderOn
            if (!isReminderOn) {
                defaultTimeUiState()
                defaultReminderUiState()
                scheduleSharedViewModel.updateSelectReminder(0)
            } else {
                setTimeUiValue(tempTime)
                setReminderUiValue(selectedReminder)
            }
        }
    }

    private fun observerSelectedReminder() {
        scheduleSharedViewModel.selectedReminder.observe(viewLifecycleOwner) { reminder ->
            selectedReminder = reminder
            if (isReminderOn) setReminderUiValue(selectedReminder)
        }
    }

    private fun observeIsTaskPinned() = with(binding) {
        dialogSharedViewModel.isPinned.observe(viewLifecycleOwner) { isPinned ->
            this@UpdateTaskBottomSheetDialog.isPinned = isPinned
            btnPin.text = if (isPinned) "Unpin Task" else "Pin Task"
            btnPin.setImage(requireContext(), if (isPinned) R.drawable.ic_pin else R.drawable.ic_unpin)
        }
    }

    private fun onClickFullDetail() = with(binding) {
        btnExpand.setOnClickListener { openFullTaskDetails() }
    }

    private fun openFullTaskDetails() {
        dismiss()
    }

    private fun setupBottomSheet() = with(binding) {
        reminderOption = requireContext().resources.getStringArray(R.array.reminder_options)
        tvDialogTitle.text = requireContext().getString(R.string.task_details)
    }

    private fun onPinAction() {
        binding.btnPin.setOnClickListener {
            val updateTask = selectedTaskHolder.copy(isPinned = !isPinned)
            dialogSharedViewModel.updateTaskPin(updateTask)
            dialogSharedViewModel.updateIsPinned(!isPinned)
        }
    }

    private fun defaultTimeUiState() = with(binding) {
        val gray = ContextCompat.getColor(requireContext(), R.color.stonewall_grey)
        tvTime.text = requireContext().getString(R.string.none)
        tvTime.setTextColor(gray)
        imgArrowCrossTime.setImage(requireContext(), R.drawable.ic_arrow_right)
    }

    private fun defaultReminderUiState() = with(binding) {
        val gray = ContextCompat.getColor(requireContext(), R.color.stonewall_grey)
        imgAlarm.colorFilter = PorterDuffColorFilter(gray, PorterDuff.Mode.SRC_IN)
        tvLabelRemind.setTextColor(gray)
        tvRemind.text = requireContext().getString(R.string.none)
        tvRemind.setTextColor(gray)
        imgArrowCrossReminder.setImage(requireContext(), R.drawable.ic_arrow_right)
        conReminder.isClickable = false
        llReminder.isClickable = false
        imgArrowCrossReminder.isClickable = false
        imgArrowCrossReminder.colorFilter = PorterDuffColorFilter(gray, PorterDuff.Mode.SRC_IN)
    }

    private fun setTitleAndDescriptionUiValue(title: String, description: String) = with(binding) {
        edtTaskName.setText(title)
        if (description.isNotEmpty()) edtDesc.setText(description)
    }

    private fun setDateUiValue(selectedDateMillis: Long) = with(binding) {
        tvDate.text = getDateIndicator(selectedDateMillis.toLocalDate())
        dialogSharedViewModel.updateIsDateValid(tvDate.text != requireContext().getString(R.string.invalid))
    }

    private fun setTimeUiValue(time: LocalTime) = with(binding) {
        val black = ContextCompat.getColor(requireContext(), R.color.black_op_77)
        tvTime.setTextColor(black)
        imgArrowCrossTime.setImage(requireContext(), R.drawable.ic_cross)
        tvTime.text = time.to12HourFormat()
    }

    private fun setReminderUiValue(selectedReminder: Int) = with(binding) {
        val black = ContextCompat.getColor(requireContext(), R.color.black_op_77)
        imgAlarm.colorFilter = PorterDuffColorFilter(black, PorterDuff.Mode.SRC_IN)
        tvLabelRemind.setTextColor(black)
        tvRemind.setTextColor(black)
        tvRemind.text = reminderOption[selectedReminder]
        conReminder.isClickable = true
        llReminder.isClickable = true
        imgArrowCrossReminder.isClickable = true
        imgArrowCrossReminder.colorFilter = PorterDuffColorFilter(black, PorterDuff.Mode.SRC_IN)
    }

    override fun onPause() {
        super.onPause()
        submitChanges()
        scheduleSharedViewModel.updateSelectDateMillis(0)
        scheduleSharedViewModel.updateIsReminderOn(false)
    }

    companion object {
        private val TAG = UpdateTaskBottomSheetDialog::class.java.simpleName
    }

}