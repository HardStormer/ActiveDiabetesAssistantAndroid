package com.example.activediabetesassistantandroidapp.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GlucoseInfoCreateViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is glucose info create"
    }
    val text: LiveData<String> = _text
}