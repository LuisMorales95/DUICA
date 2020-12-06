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
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

class SearchViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle,
        private val mainRepositoryImpl: MainRepositoryImpl,
        private val searchRepositoryImpl: SearchRepositoryImpl
) : BaseViewModel() {

    var defaultPosition = 0

    private var _localities = MutableLiveData<MutableList<Locality>>()
    val localities = Transformations.map(_localities) {
        val list = mutableListOf<String>()
        list.add("Selecciona un localidad")
        it.forEach { locality ->
            list.add(locality.nameLocality ?: "")
        }
        list
    }

    private var _suburb = MutableLiveData<MutableList<Suburb>>()
    val suburb = Transformations.map(_suburb) {
        val list = mutableListOf<String>()
        list.add("Selecciona una colonia")
        it.forEach { sub ->
            list.add(sub.nameSuburb ?: "")
        }
        list
    }

    private var _operator = mainRepositoryImpl.liveOperator
    val operator: LiveData<Operators>
        get() = _operator

    private var _liftingData = MutableLiveData<MutableList<LiftingInfo>>()
    val liftingInfo: LiveData<MutableList<LiftingInfo>>
        get() = _liftingData


    fun onStart() {
        ioThread.launch {
            _liftingData.postValue(mutableListOf())
            _localities.postValue(mutableListOf())
            _suburb.postValue(mutableListOf())
            onGetSearchSuburbs()
//            val locality = getLocalities()
//            if (locality.isSuccessful) {
//                _localities.postValue(locality.body()?.toMutableList())
//            }
        }
    }

    private suspend fun getLocalities(): Response<List<Locality>> {
        return  searchRepositoryImpl.getLocalities()
    }

    fun searchSuburbs(i: Int) {
        ioThread.launch {
            val sub = getSuburb(_localities.value?.get(i)?.idLocality ?: 0)
            if (sub.isSuccessful) {
                _suburb.postValue(sub.body()?.toMutableList())
            }
        }
    }

    fun onGetSearchSuburbs() {
        ioThread.launch {
            val sub = getSuburb(Locality.getDefault().idLocality ?: 0)
            if (sub.isSuccessful) {
                _suburb.postValue(sub.body()?.toMutableList())
            }
        }
    }

    private suspend fun getSuburb(idLocality: Int): Response<List<Suburb>> {
          return  searchRepositoryImpl.getSuburbs(idLocality)
    }

    suspend fun getLifting(suburb: Int, idResponsible: Int, idOperator: Int): Response<List<LiftingInfo>> {
           return searchRepositoryImpl.getLifting(suburb, idResponsible, idOperator)
    }

    fun searchLifting(minus: Int) {
        ioThread.launch {
            defaultPosition = minus
            val suburbSelected = _suburb.value?.get(minus)
            val lifting = getLifting(suburbSelected?.idSuburb ?: 0, _operator.value?.supervisorId
                    ?: 0, _operator.value?.operatorId ?: 0)
            if (lifting.isSuccessful) {
                _liftingData.postValue(lifting.body()?.toMutableList())
            }
        }
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

    fun onSearchLifting(operatorPosition: Int) {
        ioThread.launch {
            val operators = _operatorList.value?.get(operatorPosition - 1)
            val suburbSelected = _suburb.value?.get(defaultPosition)
            val lifting = getLifting(suburbSelected?.idSuburb ?: 0, operators?.supervisorId
                    ?: 0, operators?.operatorId ?: 0)
            if (lifting.isSuccessful) {
                _liftingData.postValue(lifting.body()?.toMutableList())
            }
        }
    }

}