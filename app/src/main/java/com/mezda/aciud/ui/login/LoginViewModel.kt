package com.mezda.aciud.ui.login

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.gson.Gson
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.data.repository.login.LoginRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import com.mezda.aciud.utils.ValueWrapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

class LoginViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private var loginRepository: LoginRepositoryImpl,
    private var mainRepositoryImpl: MainRepositoryImpl
) : BaseViewModel() {


    private val _loading = MutableLiveData<ValueWrapper<Boolean>>()
    val loading : LiveData<ValueWrapper<Boolean>>
    get() = _loading

    private val _loginSuccess = MutableLiveData<ValueWrapper<Boolean>>()
    val loginSuccess : LiveData<ValueWrapper<Boolean>>
    get() = _loginSuccess

    fun login(user: String) {
        ioThread.launch {
            _loading.postValue(ValueWrapper(true))
            val operators = getOperators().body() ?: listOf()
            _loading.postValue(ValueWrapper(false))
            if (operators.isEmpty()) {
                _messages.postValue("Ningun usuario encontrado")
            } else {
                run loop@ {
                    var found = false
                    operators.forEach {
                        if (it.user == user) {
                            found = true
                            mainRepositoryImpl.liveOperator.postValue(it)
                            _messages.postValue("Usuario Encontrado")
                            _loginSuccess.postValue(ValueWrapper(true))
                            return@loop
                        }
                    }
                    if (found) {
                        _messages.postValue("Usuario Invalido")
                    }
                }
            }
            Timber.e(Gson().toJson(operators))
        }
    }

    private suspend fun getOperators(): Response<List<Operators>> {
        return loginRepository.getOperators()
    }

    fun loggedSuccessful(){
        _loginSuccess.postValue(ValueWrapper(false))
    }

}