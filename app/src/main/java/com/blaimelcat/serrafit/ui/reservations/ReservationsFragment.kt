package com.blaimelcat.serrafit.ui.reservations

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.blaimelcat.serrafit.R
import com.blaimelcat.serrafit.databinding.FragmentReservationsBinding
import java.time.LocalDate
import java.util.*


class ReservationsFragment : Fragment() {

    private lateinit var homeViewModel: ReservationsViewModel
    private var _binding: FragmentReservationsBinding? = null
    private lateinit var datePickerDialog: DatePickerDialog
    private var globalYear: Int = 0
    private var globalMonth: Int = 0
    private var globalDay: Int = 0


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(ReservationsViewModel::class.java)

        _binding = FragmentReservationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initDatePicker(inflater, container)

        binding.buttonAddDayReservation.setOnClickListener {
            showDatePicker(inflater, container)
        }

        val args by navArgs<ReservationsFragmentArgs>()
        if (!args.admin) {
            binding.buttonAddDayReservation.visibility = View.GONE
        }

        return root
    }

    private fun initDatePicker(inflater: LayoutInflater, container: ViewGroup?) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            globalYear = year
            globalMonth = month + 1
            globalDay = dayOfMonth

            val dayOfWeek = getDayOfWeek(globalYear, globalMonth, globalDay)
            val formattedDate = formatDate(globalYear, globalMonth, globalDay, dayOfWeek)

            addSession(inflater, container, formattedDate)
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val style = android.R.style.Theme_Material_Dialog_Alert

        datePickerDialog = DatePickerDialog(context!!, style, dateSetListener, year, month, day)
    }

    private fun getDayOfWeek(selectedYear: Int, selectedMonth: Int, selectedDay: Int): String {
        return LocalDate.of(selectedYear, selectedMonth, selectedDay).dayOfWeek.name
    }

    private fun formatDate(year: Int, month: Int, day: Int, dayOfWeek: String): String {
        return "$dayOfWeek $day/$month/$year"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showDatePicker(inflater: LayoutInflater, container: ViewGroup?){
        datePickerDialog.show()
    }

    private fun addSession(inflater: LayoutInflater, container: ViewGroup?, date: String) {
        val reservation = inflater.inflate(R.layout.reservation_item, container, false)
        val dayText = reservation.findViewById<TextView>(R.id.day_text)
        dayText.text = date
        binding.reservationNest.addView(reservation)
    }
}
