package com.leomarkpaway.tactfultask.view.dialog.schedule

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.leomarkpaway.tactfultask.R

class SetReminderDialog(
    context: Context,
    defaultReminder: Int,
    private val onReminderSelected: (Int) -> Unit,
    private val onNegativeAction: () -> Unit
) : MaterialAlertDialogBuilder(context) {

    private val reminderOption: Array<String> = context.resources.getStringArray(R.array.reminder_options)
    private var selectedReminder: String = reminderOption[defaultReminder]
    private var selectedReminderIndex: Int = defaultReminder

    init {
        setTitle("Select Reminder")
        setSingleChoiceItems(reminderOption, reminderOption.indexOf(selectedReminder)) { _, which ->
            selectedReminderIndex = which
        }
        setPositiveButton("OK") { dialog, _ ->
            onReminderSelected(selectedReminderIndex)
            dialog.dismiss()
        }
        setNegativeButton("Cancel") { dialog, _ ->
            onNegativeAction()
            dialog.dismiss()
        }
    }
}