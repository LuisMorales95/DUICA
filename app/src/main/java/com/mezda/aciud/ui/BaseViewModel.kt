package com.mezda.aciud.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {

    val _messages = MutableLiveData<String>()
    val messages: LiveData<String>
        get() = _messages

    private val job = Job()
    val mainThread = CoroutineScope(Dispatchers.Main + job)

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable.message ?: "Error null")
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