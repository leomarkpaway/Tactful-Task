package com.leomarkpaway.todoapp.view.dialog.schedule

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.leomarkpaway.todoapp.R
import com.leomarkpaway.todoapp.common.enum.Pattern
import com.leomarkpaway.todoapp.common.extension.setImage
import com.leomarkpaway.todoapp.common.extension.toLocalDate
import com.leomarkpaway.todoapp.common.extension.toLocalTime
import com.leomarkpaway.todoapp.common.extension.toMillis
import com.leomarkpaway.todoapp.common.util.getCurrentDateTimeMillis
import com.leomarkpaway.todoapp.common.util.to12HourFormat
import com.leomarkpaway.todoapp.common.util.viewModelFactory
import com.leomarkpaway.todoapp.databinding.DialogFragmentSetScheduleBinding
import com.leomarkpaway.todoapp.view.dialog.schedule.viewmodel.ScheduleSharedViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Calendar

class SetScheduleBottomSheetDialog(
    private val initDateTime: Long? = null,
    private val initIsReminderOn: Boolean? = null,
    private val initReminder: Int? = null
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogFragmentSetScheduleBinding
    private val scheduleSharedViewModel: ScheduleSharedViewModel by activityViewModels {
        viewModelFactory { ScheduleSharedViewModel() }
    }

    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private var tempDateTime = ZonedDateTime.now()
    private var tempDate = LocalDate.now()
    private var tempTime = LocalTime.now()
    private lateinit var calendarView: CalendarView
    private lateinit var timePicker: MaterialTimePicker
    private var selectedDateMillis: Long = 0L
    private var isReminderOn = false
    private lateinit var reminderOption: Array<String>
    private var selectedReminder = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFragmentSetScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initDialogState()
        setupTimePicker()
        setupDateListener()
        onSetTimeAction()
        onSetReminderAction()
        onClickDone()
        onClickReset()
        observerSelectedDateMillis()
        observerIsReminderOn()
        observerSelectedReminder()
    }

    private fun initDialogState() {
        initDateTime?.let { scheduleSharedViewModel.updateSelectDateMillis(it) }
        initIsReminderOn?.let {scheduleSharedViewModel.updateIsReminderOn(it) }
        initReminder?.let { scheduleSharedViewModel.updateSelectReminder(it) }
    }

    private fun setupTimePicker() {
        val timePair = getAdjustedHour(System.currentTimeMillis())
        val hourOfDay = timePair.first
        val minute = timePair.second
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(hourOfDay)
            .setMinute(minute)
            .setTitleText("Select Time")
            .build()
    }

    private fun setupDateListener() = with(binding) {
        calendarView = binding.cvDate
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            tempDate = LocalDate.of(year, month.plus(1), dayOfMonth)
            scheduleSharedViewModel.updateSelectReminder(selectedReminder)
        }
    }

    private fun onSetTimeAction() = with(binding) {
        conTime.setOnClickListener { showDialogSetSchedule() }
        llTime.setOnClickListener { showDialogSetSchedule() }
        tempTime = LocalTime.now()

        imgArrowCross.setOnClickListener {
            if (!isReminderOn) {
                showDialogSetSchedule()
            } else {
                isReminderOn = false
                defaultTimeUiState()
                defaultReminderUiState()
            }
        }

        timePicker.addOnPositiveButtonClickListener {
            tempTime = LocalTime.of(timePicker.hour, timePicker.minute)
            scheduleSharedViewModel.updateSelectReminder(selectedReminder)
            scheduleSharedViewModel.updateIsReminderOn(true)
        }

    }

    private fun onSetReminderAction() = with(binding) {
        reminderOption = requireContext().resources.getStringArray(R.array.reminder_options)
        conReminder.setOnClickListener { showDialogSetReminder() }
        llReminder.setOnClickListener { showDialogSetReminder() }
        imgArrowCrossReminder.setOnClickListener { showDialogSetReminder() }
    }

    private fun showDialogSetSchedule() {
        timePicker.show(requireActivity().supportFragmentManager, "set_time")
    }

    private fun showDialogSetReminder() {
        SetReminderDialog(
            context = requireContext(),
            defaultReminder = selectedReminder,
            onNegativeAction = {},
            onReminderSelected = { selectedReminder ->
                this@SetScheduleBottomSheetDialog.selectedReminder = selectedReminder
                setReminderUiValue(selectedReminder)
            },
        ).show()
    }

    private fun onClickDone() {
        binding.btnDone.setOnClickListener {
            val time = if (isReminderOn) tempTime else LocalTime.MIDNIGHT
            tempDateTime = ZonedDateTime.of(tempDate, time, ZoneId.systemDefault())
            scheduleSharedViewModel.updateSelectDateMillis(tempDateTime.toMillis())
            scheduleSharedViewModel.updateIsReminderOn(isReminderOn)
            scheduleSharedViewModel.updateSelectReminder(selectedReminder)
            dismiss()
        }
    }

    private fun onClickReset() {
        binding.btnReset.setOnClickListener {
            tempDate = initDateTime?.toLocalDate() ?: getCurrentDateTimeMillis().toLocalDate()
            tempTime = initDateTime?.toLocalTime() ?: getCurrentDateTimeMillis().toLocalTime()
            tempDateTime = ZonedDateTime.of(tempDate, tempTime, ZoneId.systemDefault())
            scheduleSharedViewModel.updateSelectDateMillis(tempDateTime.toMillis())
            scheduleSharedViewModel.updateIsReminderOn(initIsReminderOn ?: isReminderOn)
            scheduleSharedViewModel.updateSelectReminder(initReminder ?: selectedReminder)
        }
    }

    private fun observerSelectedDateMillis() {
        scheduleSharedViewModel.selectDateMillis.observe(viewLifecycleOwner) { selectedDateMillis ->
            this.selectedDateMillis = selectedDateMillis
            if (selectedDateMillis != 0L) calendarView.date = selectedDateMillis
            tempTime = selectedDateMillis.toLocalTime()
            setTimeUiValue(tempTime)
        }
    }

    private fun observerIsReminderOn() {
        scheduleSharedViewModel.isReminderOn.observe(viewLifecycleOwner) { isReminderOn ->
            this@SetScheduleBottomSheetDialog.isReminderOn = isReminderOn
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

    private fun defaultTimeUiState() = with(binding) {
        val gray = ContextCompat.getColor(requireContext(), R.color.stonewall_grey)
        tvTime.text = requireContext().getString(R.string.none)
        tvTime.setTextColor(gray)
        imgArrowCross.setImage(requireContext(), R.drawable.ic_arrow_right)
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

    private fun setTimeUiValue(time: LocalTime) = with(binding) {
        val black = ContextCompat.getColor(requireContext(), R.color.black_op_77)
        tvTime.setTextColor(black)
        imgArrowCross.setImage(requireContext(), R.drawable.ic_cross)
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

    private fun getAdjustedHour(timeMillis: Long): Pair<Int, Int> {
        val timeString = timeMillis.toMillis(calendar, Pattern.TIME.id)
        val parts = timeString.split(" ")
        val timePart = parts[0]
        val isPm = parts[1].equals("pm", true)

        val timeComponents = timePart.split(":")
        val hours = timeComponents[0].toInt()
        val minutes = timeComponents[1].toInt()

        val adjustedHour = if (isPm) {
            if (hours == 12) 12 else hours + 12
        } else {
            if (hours == 12) 0 else hours
        }
        return Pair(adjustedHour, minutes)
    }

    fun resetDialogState() {
        scheduleSharedViewModel.updateSelectDateMillis(getCurrentDateTimeMillis())
        scheduleSharedViewModel.updateIsReminderOn(false)
        scheduleSharedViewModel.updateSelectReminder(0)
    }

    companion object {
        private val TAG = SetScheduleBottomSheetDialog::class.java.simpleName
    }

}
