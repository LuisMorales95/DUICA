package com.mezda.aciud.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class BaseViewModel: ViewModel() {

    val _messages = MutableLiveData<String>()
    val messages : LiveData<String>
        get() = _messages

    private val job = Job()
    val mainThread = CoroutineScope(Dispatchers.IO + job)
    val ioThread = Dispatchers.IO


    fun messageShown() {
        _messages.postValue("")
    }
}