package com.blaimelcat.serrafit.ui.reservations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReservationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is the reservations Fragment"
    }
    val text: LiveData<String> = _text
}