package com.mezda.aciud.ui.register

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mezda.aciud.data.models.Supervisor
import com.mezda.aciud.data.repository.register.RegisterRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import com.mezda.aciud.utils.ValueWrapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

class RegisterViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle,
        private val registerRepositoryImpl: RegisterRepositoryImpl
) : BaseViewModel() {

    private val _supervisors = MutableLiveData<List<Supervisor>>()
    val supervisor: LiveData<List<String>> = Transformations.map(_supervisors) {
        val stringList = mutableListOf<String>()
        stringList.add("Selecciona un responsable")
        it.forEachIndexed { _, supervisor ->
            stringList.add(supervisor.name ?: "")
        }
        return@map stringList
    }

    private val _loading = MutableLiveData<ValueWrapper<Boolean>>()
    val loading: LiveData<ValueWrapper<Boolean>>
        get() = _loading

    private val _registerSuccess = MutableLiveData<ValueWrapper<Boolean>>()
    val registerSuccess: LiveData<ValueWrapper<Boolean>>
        get() = _registerSuccess


    fun onStart() {
        ioThread.launch {
            val supervisors = getSupervisors().body() ?: listOf()
            if (supervisors.isEmpty()) {
                _messages.postValue("Sin Supervisor")
            } else {
                _supervisors.postValue(supervisors)
            }
        }
    }

    private suspend fun getSupervisors(): Response<List<Supervisor>> {
        return registerRepositoryImpl.getSupervisors()
    }

    fun onRegisterUser(user: String, password:String, name: String, supervisorId: Int) {
        ioThread.launch {
            _loading.postValue(ValueWrapper(true))
            val response = registerUser(user, password, name, supervisorId)
            _loading.postValue(ValueWrapper(false))
            if (response.isSuccessful) {
                if ((response.body() ?: 0) > 0) {
                    _messages.postValue("Solicitud Exitosa")
                    _registerSuccess.postValue(ValueWrapper(true))
                } else {
                    _messages.postValue("Solicitud Fallo")
                }
            } else {
                _messages.postValue("Solicitud Fallo")
            }
        }
    }

    private suspend fun registerUser(user: String, password: String, name: String, supervisorId: Int): Response<Int> {
        var idSupervisor = 0
        _supervisors.value?.forEachIndexed { index, supervisor ->
            if ((supervisorId - 1) == index) {
                idSupervisor = supervisor.supervisorId ?: 0
            }
        }
        return registerRepositoryImpl.postOperator(user, password, name, idSupervisor)
    }

    fun registerSuccessful() {
        _registerSuccess.postValue(ValueWrapper(false))
    }
}

