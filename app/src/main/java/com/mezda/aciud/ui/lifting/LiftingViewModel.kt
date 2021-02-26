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

    private val _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean>
        get() = _loading

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
        list.add("Selecciona una seccion")
        it.forEach {
            list.add(it.section)
        }
        list
    }

    private val _profession = MutableLiveData<List<Profession>>()
    val profession = Transformations.map(_profession){
        val list = mutableListOf<String>()
        list.add("Selecciona una profession")
        it.forEach { professions ->
            list.add(professions.profession)
        }
        list
    }

    private val _supportType = MutableLiveData<List<SupportTypes>>()
    val supportType = Transformations.map(_supportType){
        val list = mutableListOf<String>()
        list.add("Selecciona un Tipo de apoyo")
        it.forEach { support ->
            list.add(support.supportType)
        }
        list
    }

    private val _flags = MutableLiveData<List<Flag>>()
    val flags = Transformations.map(_flags){ flags ->
        val list = mutableListOf<String>()
        list.add("Selecciona un Estado")
        flags.forEach { flag ->
            list.add(flag.flag ?: "")
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
            suburb: String,
            latitude: String,
            longitude: String,
            sectionName: String,
            profession: Int,
             flag: Int,
            observations: String,
            sympathizer: Boolean,
            image: String?
    ) {
        ioThread.launch {
            _loading.postValue(true)
            var suburbIndex = 0
            run loop@ {
                _suburb.value?.forEachIndexed { index, suburbs ->
                    if (suburbs.nameSuburb == suburb){
                        suburbIndex = index
                        return@loop
                    }
                }
            }

            var sectionId = 0
            run loop@ {
                _section.value?.forEachIndexed { index, section ->
                    if (section.section == sectionName){
                        sectionId = index
                        return@loop
                    }
                }
            }

            val professionId = _profession.value?.get(profession.minus(1))?.id ?: 0
            val flagId = _flags.value?.get(flag.minus(1))?.id ?: 0

            val liftingInfo = LiftingInfo()
            liftingInfo.name = name
            liftingInfo.paternal_surname = paternal_surname
            liftingInfo.maternal_surname = maternal_surname
            liftingInfo.phone = phone
            liftingInfo.street = street
            liftingInfo.number = street_number
            liftingInfo.latitude = latitude
            liftingInfo.longitude = longitude
            liftingInfo.idSuburb = _suburb.value?.get(suburbIndex)?.idSuburb ?: 0
            liftingInfo.idOperator = _operator.value?.operatorId
            liftingInfo.idSupervisor = _operator.value?.supervisorId
//            liftingInfo.date = Date(System.currentTimeMillis()).toString()
            liftingInfo.section = _section.value?.get(sectionId)?.section
            liftingInfo.sectionId = _section.value?.get(sectionId)?.idSection
            liftingInfo.professionId = professionId
            liftingInfo.observations = observations
            liftingInfo.sympathizer = if (sympathizer) 1 else 2
            liftingInfo.idFlag = flagId
            liftingInfo.image = image ?: "null"


            val response = sendInfo(liftingInfo)
            _loading.postValue(false)
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

    fun onGetProfessions() {
        ioThread.launch {
            _profession.postValue(liftingRepositoryImpl.getProfession())
        }
    }

    fun onGetSupportType() {
        ioThread.launch {
            _supportType.postValue(liftingRepositoryImpl.getSupportType())
        }
    }

    fun onGetFlags() {
        ioThread.launch {
            _flags.postValue(liftingRepositoryImpl.getFlags())
        }
    }
}