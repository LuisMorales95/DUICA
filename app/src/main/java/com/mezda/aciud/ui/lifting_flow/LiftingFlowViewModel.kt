package com.mezda.aciud.ui.lifting_flow

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.mezda.aciud.data.models.*
import com.mezda.aciud.data.repository.lifting.LiftingRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber

class LiftingFlowViewModel @ViewModelInject constructor(
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val liftingRepositoryImpl: LiftingRepositoryImpl
) : BaseViewModel() {

    var userInfo: UserInfo = UserInfo(
        "L","L","L","9"
    )
    var directionInfo = DirectionInfo()
    var geolocationinfo = GeoLocationInfo()
    var occupationInfo = OccupationInfo()


    /**UserInfo*/
    fun saveUserInfo(
        name: String,
        paternal_last_name: String,
        maternal_last_name: String,
        phone_number: String,
        picture_encoded: String?,
        picture_uri: String?
    ) {
        userInfo = UserInfo(name, paternal_last_name, maternal_last_name, phone_number,picture_encoded = picture_encoded, picture_uri = picture_uri)
        Timber.e("saveInfo: ${Gson().toJson(userInfo)}")
    }


    /**DirectionsInfo*/
    private val _section = MutableLiveData<List<Section>>()
    val section = Transformations.map(_section){
        val list = mutableListOf<String>()
        list.add("Selecciona una seccion")
        it.forEach {
            list.add(it.section)
        }
        list
    }

    fun getSection():String {
        var sectionName = ""
        run loop@ {
            _section.value?.forEach {
                if (directionInfo.section == it.idSection){
                    sectionName = it.section
                    return@loop
                }
            }
        }
        Timber.e("getSection: $sectionName")
        return sectionName
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

    fun getSuburb():String {
        var suburbName = ""
        run loop@ {
            _suburb.value?.forEach {
                if (directionInfo.suburb == it.idSuburb){
                    suburbName = it.nameSuburb ?: ""
                    return@loop
                }
            }
        }
        Timber.e("getSuburb: $suburbName")
        return suburbName
    }

    fun onGetSections() {
        ioThread.launch {
            val sections = liftingRepositoryImpl.getSection()
            _section.postValue(sections)
        }
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

    private suspend fun getSuburbs(idLocality: Int): Response<List<Suburb>> {
        return liftingRepositoryImpl.getSuburbs(idLocality)
    }

    fun saveDirectionInfo(
        street: String,
        street_number: String,
        section: String,
        suburb:String
    ) {
        var sectionId = 0
        run loop@ {
            _section.value?.forEachIndexed { index, s ->
                if (s.section == section){
                    sectionId = index
                    return@loop
                }
            }
        }

        var suburbId = 0
        run loop@ {
            _suburb.value?.forEachIndexed { index, suburbs ->
                if (suburbs.nameSuburb == suburb){
                    suburbId = suburbs.idSuburb ?: 0
                    return@loop
                }
            }
        }

        directionInfo.apply {
            this.street = street
            this.street_number = street_number.toInt()
            this.locality = Locality.getDefault().idLocality
            this.section = sectionId
            this.suburb = suburbId
        }
    }

    /**GeoLocationInfo*/

    fun saveGeoLocation(latitude: Double, longitude:  Double){
        geolocationinfo.apply {
            this.latitude = latitude
            this.longitude = longitude
        }
    }

    /**OccupationInfo*/

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

    fun getProfessionIndex(): Int {
        var professionIndex = 0
        if (occupationInfo.profession != 0){
            run loop@ {
                _profession.value?.forEachIndexed { index, profession ->
                    if (occupationInfo.profession == profession.id ){
                        professionIndex = index + 1
                        return@loop
                    }
                }

            }
        }
        return professionIndex
    }

    fun getSupportTypeIndex(): Int {
        var supportTypeIndex = 0
        if (occupationInfo.supportTypes != 0){
            run loop@ {
                _supportType.value?.forEachIndexed { index, supportTypes ->
                    if (occupationInfo.supportTypes == supportTypes.id){
                        supportTypeIndex = index + 1
                        return@loop
                    }
                }

            }
        }
        return supportTypeIndex
    }

    fun saveOccupationInfo(
            profession_position: Int,
            supportType_position: Int,
            observations: String
    ) {
        occupationInfo.apply {
            this.profession = _profession.value?.get(profession_position - 1)?.id ?: 0
            this.supportTypes = _supportType.value?.get(supportType_position - 1)?.id ?: 0
            this.observation = observations
        }
    }

    /**PartyInfo*/

    private val _flag = MutableLiveData<List<Flag>>()
    val flag = Transformations.map(_flag){
        val list = mutableListOf<String>()
        list.add("Selecciona una Bandera")
        it.forEach { professions ->
            list.add(professions.flag ?: "")
        }
        list
    }


    fun getFlags() {
        ioThread.launch {
            _flag.postValue(liftingRepositoryImpl.getFlags())
        }
    }
}