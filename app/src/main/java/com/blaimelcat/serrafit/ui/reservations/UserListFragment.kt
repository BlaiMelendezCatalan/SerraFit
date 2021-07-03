package com.blaimelcat.serrafit.ui.reservations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.blaimelcat.serrafit.R
import com.blaimelcat.serrafit.databinding.FragmentReservationsBinding
import com.google.firebase.firestore.FirebaseFirestore


class UserListFragment : DialogFragment() {

    private lateinit var db: FirebaseFirestore
    //private var _binding: UserListFragment? = null
    //private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val inflatedLayout = inflater.inflate(R.layout.fragment_user_list, container, false)
        val tag = requireArguments().getString("tag").toString()

        db = FirebaseFirestore.getInstance()

        db.collection("training_days").document(tag).get().addOnSuccessListener { result ->
            val data = result.data
            val currUsers = data!!["users"] as ArrayList<String>
            listUsers(inflatedLayout, currUsers)
        }

        return inflatedLayout
    }

    private fun listUsers(inflatedLayout: View, users: ArrayList<String>) {
        val listView = inflatedLayout.findViewById<LinearLayout>(R.id.user_list_view)
        val buttonClose = inflatedLayout.findViewById<Button>(R.id.user_list_close_button)
        for (user in users) {
            val userTextView = TextView(context)
            userTextView.text = user
            userTextView.width = ViewGroup.LayoutParams.WRAP_CONTENT
            userTextView.height = ViewGroup.LayoutParams.WRAP_CONTENT
            listView.addView(userTextView)
        }
        //listView.invalidate()
        buttonClose.setOnClickListener {
            setFragmentResult("requestKey", bundleOf())
        }
    }
}