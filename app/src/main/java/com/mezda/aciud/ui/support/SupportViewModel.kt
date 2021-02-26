package com.mezda.aciud.ui.support

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.mezda.aciud.data.models.Support
import com.mezda.aciud.data.models.SupportTypes
import com.mezda.aciud.data.repository.support.SupportRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import com.mezda.aciud.utils.ValueWrapper
import kotlinx.coroutines.launch

class SupportViewModel @ViewModelInject constructor(
    private var supportRepository: SupportRepositoryImpl,
    private var mainRepositoryImpl: MainRepositoryImpl
) : BaseViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading
    private val _registerSuccess = MutableLiveData<ValueWrapper<Boolean>>()
    val registerSuccess: LiveData<ValueWrapper<Boolean>>
        get() = _registerSuccess


    fun registerSuccessful() {
        _registerSuccess.postValue(ValueWrapper(false))
    }

    private var liftingId: Int = 0
    private var _supportData = MutableLiveData<MutableList<Support>>()
    val supportData: LiveData<MutableList<Support>>
        get() = _supportData


    fun getSupports(id: Int) {
        liftingId = id
        ioThread.launch {
            _loading.postValue(true)
            val supports = supportRepository.getSupport(liftingId)
            _loading.postValue(false)
            _supportData.postValue(supports.toMutableList())
        }
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

    fun onGetSupportType() {
        ioThread.launch {
            _supportType.postValue(supportRepository.getSupportType())
        }
    }

    fun onSaveSupport(selectedItemPosition: Int, photoEncoded: String?) {
        ioThread.launch {
            _loading.postValue(true)
            val supportType = _supportType.value?.get(selectedItemPosition.minus(1))
            val support = Support(
                idLifting = liftingId,
                idSupportType = supportType?.id,
                supportTypeName = supportType?.supportType,
                image = photoEncoded
            )
            val response = supportRepository.sendSupport(support)
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

    fun onDelete(idSupport: Int?) {
        ioThread.launch {
            _loading.postValue(true)
            val response = idSupport?.let { supportRepository.deleteSupport(it) }
            _loading.postValue(false)
            if ((response?.body()?: 0) != 0) {
                _messages.postValue("Se borro el apoyo")
                getSupports(liftingId)
            } else {
                _messages.postValue("Error no se pudo borrar el apoyo")
            }
        }
    }
}