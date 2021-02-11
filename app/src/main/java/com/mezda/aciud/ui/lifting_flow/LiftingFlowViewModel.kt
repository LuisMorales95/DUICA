package com.mezda.aciud.ui.lifting_flow

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.google.gson.Gson
import com.mezda.aciud.data.models.*
import com.mezda.aciud.data.repository.lifting.LiftingRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import com.mezda.aciud.utils.ValueWrapper
import kotlinx.coroutines.launch
import retrofit2.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LiftingFlowViewModel @ViewModelInject constructor(
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val liftingRepositoryImpl: LiftingRepositoryImpl
) : BaseViewModel() {

    private var liftingInfo: LiftingInfo? = null

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading
    private val _registerSuccess = MutableLiveData<ValueWrapper<Boolean>>()
    val registerSuccess: LiveData<ValueWrapper<Boolean>>
        get() = _registerSuccess


    fun registerSuccessful() {
        _registerSuccess.postValue(ValueWrapper(false))
    }

    /**UserInfo*/

    var userInfo: UserInfo = UserInfo(
//        "Luis", "Morales", "Perez", "9212222222"
    )

    fun saveUserInfo(
        name: String,
        paternal_last_name: String,
        maternal_last_name: String,
        phone_number: String,
        picture_encoded: String?,
        picture_uri: String?
    ) {
        userInfo = UserInfo(
            name,
            paternal_last_name,
            maternal_last_name,
            phone_number,
            picture_encoded = picture_encoded,
            picture_uri = picture_uri
        )
        Timber.e("saveInfo: ${Gson().toJson(userInfo)}")
    }


    /**DirectionsInfo*/

    var directionInfo = DirectionInfo(
//        "Araucaria", 10, "COATZACOALCOS", 1, "4723", 215, "20 DE NOVIEMBRE               ", 2
    )

    private val _section = MutableLiveData<List<Section>>()
    val section = Transformations.map(_section) {
        val list = mutableListOf<String>()
        list.add("Selecciona una seccion")
        it.forEach {
            list.add(it.section)
        }
        list
    }

    fun getSection(): String {
        var sectionName = ""
        run loop@{
            _section.value?.forEach {
                if (directionInfo.sectionId == it.idSection) {
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

    fun getSuburb(): String {
        var suburbName = ""
        run loop@{
            _suburb.value?.forEach {
                if (directionInfo.suburbId == it.idSuburb) {
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
        suburb: String
    ) {
        var sectionId = 0
        run loop@{
            _section.value?.forEachIndexed { index, s ->
                if (s.section == section) {
                    sectionId = index
                    return@loop
                }
            }
        }

        var suburbId = 0
        run loop@{
            _suburb.value?.forEachIndexed { _, suburbs ->
                if (suburbs.nameSuburb == suburb) {
                    suburbId = suburbs.idSuburb ?: 0
                    return@loop
                }
            }
        }

        directionInfo.apply {
            this.street = street
            this.street_number = street_number.toInt()
            this.locality = Locality.getDefault().nameLocality
            this.localityId = Locality.getDefault().idLocality
            this.section = section
            this.sectionId = sectionId
            this.suburb = suburb
            this.suburbId = suburbId
        }
        Timber.e("directionInfo: ${Gson().toJson(directionInfo)}")
    }

    /**GeoLocationInfo*/

    var geolocationinfo = GeoLocationInfo(
//        37.4219983, -122.084
    )

    fun getGetLocation() = geolocationinfo

    fun saveGeoLocation(latitude: Double, longitude: Double) {
        geolocationinfo.apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        Timber.e("geolocationinfo: ${Gson().toJson(geolocationinfo)}")
    }

    /**OccupationInfo*/

    var occupationInfo = OccupationInfo(
//        3, "Fotógrafo", 10, "Apoyo microempresas", "testing occupation"
    )
    var partyInfo = PartyInfo(
//        2, 4, "Problemático "
    )

    private val _profession = MutableLiveData<List<Profession>>()
    val profession = Transformations.map(_profession) {
        val list = mutableListOf<String>()
        list.add("Selecciona una profession")
        it.forEach { professions ->
            list.add(professions.profession)
        }
        list
    }

    private val _supportType = MutableLiveData<List<SupportTypes>>()
    val supportType = Transformations.map(_supportType) {
        val list = mutableListOf<String>()
        list.add("Selecciona un Tipo de apoyo")
        it.forEach { support ->
            list.add(support.supportType)
        }
        list
    }

    private val _flag = MutableLiveData<List<Flag>>()
    val flag = Transformations.map(_flag) {
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
        if (occupationInfo.professionId != 0) {
            run loop@{
                _profession.value?.forEachIndexed { index, profession ->
                    if (occupationInfo.professionId == profession.id) {
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
        if (occupationInfo.supportTypesId != 0) {
            run loop@{
                _supportType.value?.forEachIndexed { index, supportTypes ->
                    if (occupationInfo.supportTypesId == supportTypes.id) {
                        supportTypeIndex = index + 1
                        return@loop
                    }
                }

            }
        }
        return supportTypeIndex
    }

    fun getStatus4T() = partyInfo.status_4t ?: 1

    fun getFlagIndex(): Int {
        var flagIndex = 0
        if (partyInfo.flagId != 0) {
            run loop@{
                _flag.value?.forEachIndexed { index, flag ->
                    if (partyInfo.flagId == flag.id) {
                        flagIndex = index + 1
                        return@loop
                    }
                }
            }
        }
        return flagIndex
    }

    fun getObservations() = occupationInfo.observation ?: ""

    fun saveOccupationInfo(
        profession_position: Int,
        supportType_position: Int,
        statusId: Int,
        flagPosition: Int,
        observations: String
    ) {
        ioThread.launch {
            partyInfo.apply {
                status_4t = statusId
                flagId = _flag.value?.get(flagPosition - 1)?.id ?: 0
                flag = _flag.value?.get(flagPosition - 1)?.flag ?: ""
            }
            Timber.e("partyInfo: ${Gson().toJson(partyInfo)}")
            occupationInfo.apply {
                this.professionId = _profession.value?.get(profession_position - 1)?.id ?: 0
                this.profession = _profession.value?.get(profession_position - 1)?.profession ?: ""
                this.supportTypesId = _supportType.value?.get(supportType_position - 1)?.id ?: 0
                this.supportTypes =
                    _supportType.value?.get(supportType_position - 1)?.supportType ?: ""
                this.observation = observations
            }
            Timber.e("occupationInfo: ${Gson().toJson(occupationInfo)}")
        }
    }
    val format = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
//    val format = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"
    @SuppressLint("SimpleDateFormat")
    fun sendLifting() {
        ioThread.launch {
            _loading.postValue(true)
            try {
                liftingInfo = liftingInfo ?: LiftingInfo()
                liftingInfo?.name = userInfo.name
                liftingInfo?.paternal_surname = userInfo.paternal_last_name
                liftingInfo?.maternal_surname = userInfo.maternal_last_name
                liftingInfo?.phone = userInfo.phone_number
                liftingInfo?.image = userInfo.picture_encoded ?: "null"

                liftingInfo?.street = directionInfo.street
                liftingInfo?.number = directionInfo.street_number.toString()
                liftingInfo?.section = directionInfo.section
                liftingInfo?.sectionId = directionInfo.sectionId

                liftingInfo?.latitude = geolocationinfo.latitude.toString()
                liftingInfo?.longitude = geolocationinfo.longitude.toString()

                liftingInfo?.supportTypeId = occupationInfo.supportTypesId
                liftingInfo?.professionId = occupationInfo.professionId
                liftingInfo?.observations = occupationInfo.observation
                liftingInfo?.sympathizer = partyInfo.status_4t
                liftingInfo?.idFlag = partyInfo.flagId

                val dateFormat = SimpleDateFormat(format)
                val formattedDate = dateFormat.format(Date(System.currentTimeMillis()))
                liftingInfo?.date = formattedDate
                liftingInfo?.idSuburb = directionInfo.suburbId
                liftingInfo?.idOperator = mainRepositoryImpl.liveOperator.value?.operatorId
                liftingInfo?.idSupervisor = mainRepositoryImpl.liveOperator.value?.supervisorId
                Timber.e("liftingInfo: ${Gson().toJson(liftingInfo)}")



                val response = sendInfo(liftingInfo!!)
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
            } catch (ex: Exception) {
                _loading.postValue(false)
                Timber.e(ex)
            }
        }
    }


    private suspend fun sendInfo(liftingInfo: LiftingInfo): Response<Int> {
        return liftingRepositoryImpl.sendLifting(liftingInfo)
    }

    fun mapLiftingToModels(lifting: LiftingInfo) {
        this.liftingInfo = liftingInfo
        userInfo = UserInfo(
            lifting.name ?: "",
            lifting.paternal_surname ?: "",
            lifting.maternal_surname ?: "",
            lifting.phone ?: "",
            picture_url = lifting.image
        )

        directionInfo = DirectionInfo(
            lifting.street,
            (lifting.number ?: "0").toInt(),
            locality = Locality.getDefault().nameLocality,
            localityId = Locality.getDefault().idLocality,
            sectionId = lifting.sectionId,
            section = lifting.section,
            suburbId = lifting.idSuburb
        )

        geolocationinfo = GeoLocationInfo(
            (lifting.latitude ?: "0.0").toDouble(),
            (lifting.longitude ?: "0.0").toDouble()
        )

        occupationInfo = OccupationInfo(
            lifting.professionId,
            "",
            supportTypesId = lifting.supportTypeId,
            supportTypes = "",
            observation = lifting.observations
        )

        partyInfo = PartyInfo(
            lifting.sympathizer,
            lifting.idFlag,
            ""
        )
    }
}