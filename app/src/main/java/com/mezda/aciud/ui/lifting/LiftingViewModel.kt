package com.mezda.aciud.ui.lifting

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.gson.Gson
import com.mezda.aciud.data.models.Locality
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.data.models.Suburb
import com.mezda.aciud.data.models.Supervisor
import com.mezda.aciud.data.repository.lifting.LiftingRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import com.mezda.aciud.utils.ValueWrapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber

class LiftingViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val liftingRepositoryImpl: LiftingRepositoryImpl
) : BaseViewModel() {

    private val _localities = MutableLiveData<List<Locality>>()
    val localities = Transformations.map(_localities) {
        val list = mutableListOf<String>()
        list.add("Selecciona un localidad")
        it.forEach { locality ->
            list.add(locality.nameLocality ?: "")
        }
        list
    }

    private val _suburb = MutableLiveData<List<Suburb>>()
    val suburb = Transformations.map(_suburb) {
        val list = mutableListOf<String>()
        list.add("Selecciona una colonia")
        it.forEach {
            list.add(it.nameSuburb ?: "")
        }
        list
    }

    private val _supervisor = MutableLiveData<Supervisor>()
    val supervisor: LiveData<Supervisor>
        get() = _supervisor

    private val _operator = MutableLiveData<Operators>()
    val operator: LiveData<Operators>
        get() = _operator

    fun onGetLocalities() {
        viewModelScope.launch {
            val localities = getLocalities()
            if (localities.isSuccessful) {
                _localities.postValue(localities.body() ?: listOf())
            } else {
                _localities.postValue(listOf())
            }
        }
    }

    private suspend fun getLocalities(): Response<List<Locality>> {
        return withContext(ioThread) {
            liftingRepositoryImpl.getLocalities()
        }
    }

    fun onLocalitySelected(position: Int) {
        viewModelScope.launch {
            if (position < 0) {
                _suburb.postValue(listOf())
            } else {
                var idLocality = 0
                _localities.value?.forEachIndexed { index, loc ->
                    if ((position - 1) == index) {
                        idLocality = loc.idLocality ?: 0
                    }
                }
                val suburbsResponse = getSuburbs(idLocality)
                if (suburbsResponse.isSuccessful) {
                    _suburb.postValue(suburbsResponse.body() ?: listOf())
                } else {
                    _suburb.postValue(listOf())
                }
            }
        }
    }

    private suspend fun getSuburbs(idLocality: Int): Response<List<Suburb>> {
        return withContext(ioThread) {
            liftingRepositoryImpl.getSuburbs(idLocality)
        }
    }

    fun onGetOperator() {
        viewModelScope.launch {
            _operator.postValue(mainRepositoryImpl.liveOperator.value?.get())
        }
    }

    fun onGetSupervisor() {
        viewModelScope.launch {
            val supervisors = getSupervisor()
            if (supervisors.isSuccessful) {
                Timber.e(Gson().toJson(supervisors.body()))
                (supervisors.body() ?: listOf()).forEach { sv ->
                    if (operator.value?.supervisorId == sv.supervisorId) {
                        _supervisor.postValue(sv)
                    }
                }
            }
        }
    }

    private suspend fun getSupervisor(): Response<List<Supervisor>> {
        return withContext(ioThread) {
            liftingRepositoryImpl.getSupervisors()
        }
    }
}