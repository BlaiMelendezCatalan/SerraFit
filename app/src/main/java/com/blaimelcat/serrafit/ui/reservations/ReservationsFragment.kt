package com.blaimelcat.serrafit.ui.reservations

import com.blaimelcat.serrafit.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.blaimelcat.serrafit.databinding.FragmentReservationsBinding


class ReservationsFragment : Fragment() {

    private lateinit var homeViewModel: ReservationsViewModel
    private var _binding: FragmentReservationsBinding? = null

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

        binding.buttonAddSessionReservation.setOnClickListener {
            addSession(inflater, container)
        }

        val args by navArgs<ReservationsFragmentArgs>()
        if (!args.admin) {
            binding.buttonAddSessionReservation.visibility = View.GONE
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addSession(inflater: LayoutInflater, container: ViewGroup?){
        val reservation = inflater.inflate(R.layout.reservation_module, container, false)
        binding.reservationNest.addView(reservation)
    }
}
