package com.mezda.aciud.ui.users

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.data.repository.user.UserRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import kotlinx.coroutines.launch

class UserViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private var userRepositoryImpl: UserRepositoryImpl
) : BaseViewModel() {

    private val _users = MutableLiveData<List<Operators>>()
    val users: LiveData<List<Operators>>
        get() = _users


    fun getOperators() {
        ioThread.launch {
            val response = userRepositoryImpl.getOperators()
            if (response.isSuccessful) {
                _users.postValue(response.body())
            } else {
                _users.postValue(listOf())
                _messages.postValue("Solicitud Fallida")
            }
        }
    }

    fun updateOperator(operators: Operators) {
        ioThread.launch {
            val response = userRepositoryImpl.updateOperator(operators)
            if (response.isSuccessful){
                if ((response.body()?:0) == 0) {
                    _messages.postValue("Accion no pudo ser completada")
                } else {
                    getOperators()
                }
            } else {
                _messages.postValue("Actualizacion Fallida")
            }
        }
    }
}