package com.mezda.aciud.ui.lifting

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.gson.Gson
import com.mezda.aciud.data.models.*
import com.mezda.aciud.data.repository.lifting.LiftingRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import com.mezda.aciud.utils.ValueWrapper
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.util.*

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

    private val _section = MutableLiveData<List<Section>>()
    val section = Transformations.map(_section){
        val list = mutableListOf<String>()
        it.forEach {
            list.add(it.section)
        }
        list
    }

    private val _supervisor = MutableLiveData<Supervisor>()
    val supervisor: LiveData<Supervisor>
        get() = _supervisor

    private val _operator = MutableLiveData<Operators>()
    val operator: LiveData<Operators>
        get() = _operator

    private val _registerSuccess = MutableLiveData<ValueWrapper<Boolean>>()
    val registerSuccess: LiveData<ValueWrapper<Boolean>>
        get() = _registerSuccess

    fun onGetLocalities() {
        ioThread.launch {
            val localities = getLocalities()
            if (localities.isSuccessful) {
                _localities.postValue(localities.body() ?: listOf())
            } else {
                _localities.postValue(listOf())
            }
        }
    }

    private suspend fun getLocalities(): Response<List<Locality>> {
        return  liftingRepositoryImpl.getLocalities()
    }

    fun onLocalityByDefault() {
        ioThread.launch {
            val suburbsResponse = getSuburbs(Locality.getDefault().idLocality ?: 0)
            if (suburbsResponse.isSuccessful) {
                _suburb.postValue(suburbsResponse.body() ?: listOf())
            } else {
                _suburb.postValue(listOf())
            }
        }
    }

    fun onLocalitySelected(position: Int) {
        ioThread.launch {
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
        return liftingRepositoryImpl.getSuburbs(idLocality)
    }

    fun onGetOperator() {
        ioThread.launch {
            _operator.postValue(mainRepositoryImpl.liveOperator.value)
        }
    }

    fun onGetSupervisor() {
        ioThread.launch {
            val supervisors = getSupervisor()
            if (supervisors.isSuccessful) {
                Timber.e(Gson().toJson(supervisors.body()))
                run loop@{
                    (supervisors.body() ?: listOf()).forEach { sv ->
                        if (operator.value?.supervisorId == sv.supervisorId) {
                            _supervisor.postValue(sv)
                            return@loop
                        }
                    }
                }

            }
        }
    }

    private suspend fun getSupervisor(): Response<List<Supervisor>> {
        return liftingRepositoryImpl.getSupervisors()
    }

    fun onGetSections() {
        ioThread.launch {
            val sections = liftingRepositoryImpl.getSection()
            _section.postValue(sections)
        }
    }

    fun sendLiftingInfo(
            name: String,
            paternal_surname: String,
            maternal_surname: String,
            phone: String,
            street: String,
            street_number: String,
            locality: Int,
            suburb: Int,
            latitude: String,
            longitude: String,
            section: String
    ) {
        ioThread.launch {

            var sectionObject: Section? = null
            run loop@ {
                _section.value?.forEach {
                    if (section == it.section){
                        sectionObject = it
                        return@loop
                    }
                }
            }
            val liftingInfo = LiftingInfo()
            liftingInfo.name = name
            liftingInfo.paternal_surname = paternal_surname
            liftingInfo.maternal_surname = maternal_surname
            liftingInfo.phone = phone
            liftingInfo.street = street
            liftingInfo.number = street_number
            liftingInfo.latitude = latitude
            liftingInfo.longitude = longitude
            liftingInfo.idSuburb = _suburb.value?.get(suburb - 1)?.idSuburb ?: 0
            liftingInfo.idOperator = _operator.value?.operatorId
            liftingInfo.idSupervisor = _operator.value?.supervisorId
            liftingInfo.date = Date(System.currentTimeMillis()).toString()
            liftingInfo.section = sectionObject?.section
            liftingInfo.sectionId = sectionObject?.idSection
            val response = sendInfo(liftingInfo)
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

    private suspend fun sendInfo(liftingInfo: LiftingInfo): Response<Int> {
        return liftingRepositoryImpl.sendLifting(liftingInfo)
    }

    fun registerSuccessful() {
        _registerSuccess.postValue(ValueWrapper(false))
    }
}