package com.mezda.aciud.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

abstract class BaseViewModel : ViewModel() {

    val _messages = MutableLiveData<String>()
    val messages: LiveData<String>
        get() = _messages

    private val job = Job()
    val mainThread = CoroutineScope(Dispatchers.Main + job)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _messages.postValue(throwable.message)
        _messages.postValue("")
    }
    val ioThread = CoroutineScope(Dispatchers.IO + exceptionHandler)


    fun messageShown() {
        _messages.postValue("")
    }

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

}