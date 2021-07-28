package com.blaimelcat.serrafit.ui.reservations

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.blaimelcat.serrafit.R
import com.blaimelcat.serrafit.databinding.FragmentReservationsBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.*
import kotlin.collections.ArrayList


class ReservationsFragment : Fragment() {

    private lateinit var homeViewModel: ReservationsViewModel
    private var _binding: FragmentReservationsBinding? = null
    private lateinit var db: FirebaseFirestore
    private val sessionCreationFragment = SessionCreationFragment()
    private val userListFragment = UserListFragment()
    private var admin: Boolean = false
    private var currentUsername: String = ""
    private val ORANGE = floatArrayOf(32F, 98F, 91F)
    private val PURPLE = floatArrayOf(265F, 100F, 47F)
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

        db = FirebaseFirestore.getInstance()

        binding.buttonAddReservation.setOnClickListener {
            setFragmentResultListener("requestKey") { requestKey, bundle ->
                // We use a String here, but any type that can be put in a Bundle is supported
                val trainingDate = bundle.getString("trainingDate")
                val trainingTime = bundle.getString("trainingTime")
                val capacity = bundle.getInt("capacity")

                sessionCreationFragment.dismiss()

                addNewSession(trainingDate!!, trainingTime!!, capacity, inflater, container)
            }
            sessionCreationFragment.show(parentFragmentManager, "")
        }

        val sharedPref = activity!!.getPreferences(Context.MODE_PRIVATE)
        admin = sharedPref.getBoolean("admin", false)
        if (!admin) {
            binding.buttonAddReservation.visibility = View.GONE
        }

        val args by navArgs<ReservationsFragmentArgs>()
        currentUsername = args.currentUser

        loadTrainingDays(inflater, container, db)

        return root
    }

    private fun addNewSession(trainingDate: String, trainingTime: String, capacity: Int,
                   inflater: LayoutInflater, container: ViewGroup?) {
        val timeKey = formatTimeFromTextToKey(trainingTime)
        val dateKey = formatDateFromTextToKey(trainingDate)
        val tag = "$dateKey$timeKey"
        db.collection("training_days").document(tag).set(
            hashMapOf(
                "tag" to tag,
                "trainingDate" to trainingDate,
                "trainingTime" to trainingTime,
                "occupation" to 0,
                "maxCapacity" to capacity,
                "users" to listOf<String>()
            ),
            SetOptions.merge()
        )
        loadTrainingDays(inflater, container, db)
    }

    private fun formatDateFromTextToKey(dayText: String): String {
        val args = dayText.split(" ")[1].split("/")
        var day = args[0]
        var month = (args[1].toInt() + 1).toString()
        val year = args[2]

        if (month.length == 1) {
            month = "0$month"
        }

        if (day.length == 1) {
            day = "0$day"
        }

        return "$year$month$day"
    }

    private fun formatTimeFromTextToKey(trainingTime: String):  String {
        val args = trainingTime.split(":")
        var hour = args[0]
        var minute = args[1]

        return "$hour$minute"
    }

    private fun loadTrainingDays(inflater: LayoutInflater, container: ViewGroup?,
                                 db: FirebaseFirestore) {

        fun nonAdminClickListener(reservationButton: Button, db: FirebaseFirestore) {
            val currentTag = reservationButton.tag.toString()
            var currTrainingDate: String = ""
            var currTrainingTime: String = ""
            var currOccupation: Long = 0
            var currMaxCapacity: Long = 0
            var currUsers: ArrayList<String>
            db.collection("training_days").document(currentTag).get()
                .addOnSuccessListener { result ->
                val data = result.data
                currTrainingDate = data!!["trainingDate"].toString()
                currTrainingTime = data["trainingTime"].toString()
                currOccupation = data["occupation"] as Long
                currMaxCapacity = data["maxCapacity"] as Long
                currUsers = data["users"] as ArrayList<String>

                // Check if session is full
                if (currOccupation >= currMaxCapacity) {
                    showSessionFullAlert()
                    return@addOnSuccessListener
                }
                // If user has already booked this session, remove it
                if (currentUsername in currUsers) {
                    currUsers.remove(currentUsername)
                    val updatedOccupation = currUsers.size
                    val updateHashMap = hashMapOf(
                        "occupation" to updatedOccupation,
                        "users" to currUsers
                    )
                    db.collection("training_days").document(currentTag).update(
                        updateHashMap as Map<String, Any>)
                    val newText = "$currTrainingDate   $currTrainingTime\n$updatedOccupation/$currMaxCapacity"
                    reservationButton.text = newText
                    reservationButton.setBackgroundColor(Color.HSVToColor(PURPLE))
                // If user has not booked this session yet, add it
                } else {
                    currUsers.add(currentUsername)
                    val updatedOccupation = currUsers.size
                    val updateHashMap = hashMapOf(
                        "occupation" to updatedOccupation,
                        "users" to currUsers
                    )
                    db.collection("training_days").document(currentTag).update(
                        updateHashMap as Map<String, Any>)
                    val newText = "$currTrainingDate   $currTrainingTime\n$updatedOccupation/$currMaxCapacity"
                    reservationButton.text = newText
                    reservationButton.setBackgroundColor(Color.HSVToColor(ORANGE))
                }
            }
        }

        fun adminClickListener(reservationButton: Button, db: FirebaseFirestore) {
            val currentTag = reservationButton.tag.toString()
            val bundle = bundleOf(
                "tag" to currentTag
            )
            userListFragment.arguments = bundle
            setFragmentResultListener("requestKey") { _, _ ->
                userListFragment.dismiss()
            }
            userListFragment.show(parentFragmentManager, "")
        }

        fun addExistingSession(tag: String, trainingDate: String, trainingTime: String,
                               occupation: Int, maxCapacity: Int, users: ArrayList<String>,
                               inflater: LayoutInflater, container: ViewGroup?) {
            val reservationButtonView = inflater.inflate(R.layout.reservation_button, container,false)
            val reservationButton = reservationButtonView.findViewById<Button>(R.id.reservation_button)
            reservationButton.tag = tag
            reservationButton.text= "$trainingDate   $trainingTime\n$occupation/$maxCapacity"
            if (currentUsername in users) {
                reservationButton.setBackgroundColor(Color.HSVToColor(ORANGE))
            } else {
                reservationButton.setBackgroundColor(Color.HSVToColor(PURPLE))
            }
            reservationButton.setOnClickListener {
                if (admin) {
                    adminClickListener(reservationButton, db)
                } else {
                    nonAdminClickListener(reservationButton, db)
                }
            }
            binding.reservationNest.addView(reservationButton)
        }

        binding.reservationNest.removeAllViews()

        db.collection("training_days").get().addOnSuccessListener { result ->
            for (document in result.documents) {
                val data = document.data
                val tag = data!!["tag"].toString()
                val trainingDate = data!!["trainingDate"].toString()
                val trainingTime = data!!["trainingTime"].toString()
                val occupation = data!!["occupation"] as Long
                val maxCapacity = data!!["maxCapacity"] as Long
                val users = data!!["users"] as ArrayList<String>
                addExistingSession(tag, trainingDate, trainingTime, occupation.toInt(),
                                   maxCapacity.toInt(), users, inflater, container
                )
            }
        }
    }

    private fun showSessionFullAlert() {
        AlertDialog.Builder(context!!)
            .setTitle("Unable to book session")
            .setMessage("This session is already full")
            .setNeutralButton("OK", null).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
