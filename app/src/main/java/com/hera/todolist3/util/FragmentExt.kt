package com.hera.todolist3.util

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import java.util.*

fun Fragment.showDatePickerDialog(onDateSetListener: DatePickerDialog.OnDateSetListener) {
    val cal = Calendar.getInstance()
    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH)
    val day = cal.get(Calendar.DAY_OF_MONTH)
    val dialog = DatePickerDialog(
        requireContext(),
        onDateSetListener,
        year, month, day
    )
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
    dialog.show()
}