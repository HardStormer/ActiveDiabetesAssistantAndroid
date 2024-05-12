package com.example.activediabetesassistantandroidapp.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PersonInfoUpdateViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is person info update"
    }
    val text: LiveData<String> = _text
}