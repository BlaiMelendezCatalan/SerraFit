package com.blaimelcat.serrafit.ui.reservations

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.blaimelcat.serrafit.R
import java.time.LocalDate
import java.util.*


class SessionCreationFragment: DialogFragment() {

    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog

    private var trainingDate: String = ""
    private var trainingTime: String = ""
    private var capacity: Int = 0

    private val capacityMaxValue = 100
    private val capacityMinValue = 1
    private val capacityWrapSelectorWheel = false
    private val capacityValue = 20


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflatedLayout = inflater.inflate(R.layout.fragment_session_creation, container, false)
        val dateContentText = inflatedLayout.findViewById<TextView>(R.id.date_content_text)
        dateContentText.setOnClickListener {
            showDatePicker()
        }
        val timeContentText = inflatedLayout.findViewById<TextView>(R.id.time_content_text)
        timeContentText.setOnClickListener {
            showTimePicker()
        }
        val capacityContentText = inflatedLayout.findViewById<NumberPicker>(R.id.capacity_content_text)
        val acceptButton = inflatedLayout.findViewById<Button>(R.id.accept_session_button)
        acceptButton.setOnClickListener {
            if (trainingDate != "" && trainingTime != "") {
                // Use the Kotlin extension in the fragment-ktx artifact
                setFragmentResult(
                    "requestKey", bundleOf(
                        "trainingDate" to trainingDate,
                        "trainingTime" to trainingTime,
                        "capacity" to capacity
                    )
                )
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Error")
                    .setMessage("All fields are required")
                    .setNeutralButton("OK", null).show()
            }
        }

        // Initialize
        initDatePicker(dateContentText)
        initTimePicker(timeContentText)
        initNumberPicker(capacityContentText)

        return inflatedLayout
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initDatePicker(dateContentText: TextView) {

        fun formatDate(year: Int, month: Int, day: Int, dayOfWeek: String): String {
            return "$dayOfWeek $day/$month/$year"
        }

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val dayOfWeek = getDayOfWeek(year, month + 1, dayOfMonth)
            trainingDate = formatDate(year, month + 1, dayOfMonth, dayOfWeek)
            dateContentText.text = trainingDate
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val style = android.R.style.Theme_Material_Dialog_Alert

        val dayOfWeek = getDayOfWeek(year, month + 1, day)
        val formattedDate = formatDate(year, month + 1, day, dayOfWeek)
        dateContentText.hint = formattedDate
        trainingDate = formattedDate
        datePickerDialog = DatePickerDialog(requireContext(), style, dateSetListener, year, month, day)
    }

    private fun showDatePicker(){
        datePickerDialog.show()
    }

    private fun initTimePicker(timeContentText: TextView) {

        fun formatTime(hour_: String, minute_: String):  String {
            var hour = hour_
            var minute = minute_
            if (hour.length == 1) {
                hour = "0$hour"
            }
            if (minute.length == 1) {
                minute = "0$minute"
            }
            return "$hour:$minute"
        }

        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            trainingTime = formatTime(hourOfDay.toString(), minute.toString())
            timeContentText.text = trainingTime
        }

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val formattedTime = formatTime(hour.toString(), minute.toString())
        timeContentText.hint = formattedTime
        trainingTime = formattedTime
        timePickerDialog = TimePickerDialog(requireContext(), timeSetListener, hour, minute, true)

    }

    private fun showTimePicker(){
        timePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDayOfWeek(selectedYear: Int, selectedMonth: Int, selectedDay: Int): String {
        return LocalDate.of(selectedYear, selectedMonth, selectedDay).dayOfWeek.name
    }

    private fun initNumberPicker(capacityContentText: NumberPicker) {

        capacityContentText.setOnValueChangedListener { _, _, newVal ->
            capacity = newVal
        }

        capacityContentText.maxValue = capacityMaxValue
        capacityContentText.minValue = capacityMinValue
        capacityContentText.wrapSelectorWheel = capacityWrapSelectorWheel
        capacityContentText.value = capacityValue

        capacity = capacityValue
    }
}