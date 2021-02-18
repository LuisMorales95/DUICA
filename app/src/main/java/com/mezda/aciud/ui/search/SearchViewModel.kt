package com.mezda.aciud.ui.search

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.mezda.aciud.data.models.*
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import com.mezda.aciud.data.repository.search.SearchRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import com.mezda.aciud.utils.ValueWrapper
import kotlinx.coroutines.launch
import retrofit2.Response

class SearchViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val searchRepositoryImpl: SearchRepositoryImpl
) : BaseViewModel() {

    private var _operator = mainRepositoryImpl.liveOperator
    val operator: LiveData<Operators>
        get() = _operator

    private var _liftingData = MutableLiveData<MutableList<LiftingInfo>>()
    val liftingInfo: LiveData<MutableList<LiftingInfo>>
        get() = _liftingData


    fun onStart() {
        ioThread.launch {
            _liftingData.postValue(mutableListOf())
            findLifting()
            refreshOperator()
        }
    }

    fun refreshOperator() {
        ioThread.launch {
            val operators = searchRepositoryImpl.getOperators().body() ?: listOf()
            if (operators.isEmpty()) {
                _messages.postValue("Sin usuarios")
            } else {
                run loop@{
                    var found = false
                    operators.forEach {
                        if (_operator.value?.operatorId == it.operatorId) {
                            found = true
                            mainRepositoryImpl.liveOperator.postValue(it)
                            return@loop
                        }
                    }
                    if (!found) {
                        _messages.postValue("Tu usuario no se encontro")
                    } else {
                        _messages.postValue("User found")
                    }
                }
            }
        }

    }

    private fun findLifting() {
        ioThread.launch {
            val lifting = getLifting(
                "null", _operator.value?.supervisorId
                    ?: 0, _operator.value?.operatorId ?: 0
            )
            if (lifting.isSuccessful) {
                _liftingData.postValue(lifting.body()?.reversed()?.toMutableList())
            }
        }
    }

    private suspend fun getLifting(
        suburb: String?,
        idResponsible: Int,
        idOperator: Int
    ): Response<List<LiftingInfo>> {
        return searchRepositoryImpl.getLifting(suburb, idResponsible, idOperator)
    }

    private var _operatorList = MutableLiveData<MutableList<Operators>>()
    val operatorList = Transformations.map(_operatorList) {
        val list = mutableListOf<String>()
        list.add("Selecciona un operator")
        it.forEach { operators ->
            list.add("${operators.user} ${operators.name}")
        }
        list
    }

    fun onOperator() {
        ioThread.launch {
            _operatorList.postValue(mutableListOf())
            val operators = getOperators()
            if (operators.isSuccessful) {
                _operatorList.postValue(operators.body()?.toMutableList())
            }
        }
    }

    private suspend fun getOperators(): Response<List<Operators>> {
        return searchRepositoryImpl.getOperators()
    }

    fun onSearch(operatorPosition: Int = 0, isAdmin: Boolean = false) {
        ioThread.launch {
            if (isAdmin) {
                val lifting = getLifting(
                    "null",
                    _operatorList.value?.get(operatorPosition - 1)?.supervisorId ?: 0,
                    _operatorList.value?.get(operatorPosition - 1)?.operatorId ?: 0
                )
                if (lifting.isSuccessful) {
                    _liftingData.postValue(lifting.body()?.toMutableList())
                }
            } else {
                val lifting = getLifting(
                    "null",
                    operator.value?.supervisorId ?: 0,
                    operator.value?.operatorId ?: 0
                )
                if (lifting.isSuccessful) {
                    _liftingData.postValue(lifting.body()?.toMutableList())
                }
            }
        }
    }
}