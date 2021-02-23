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
import kotlinx.coroutines.launch

class SupportViewModel @ViewModelInject constructor(
    private var supportRepository: SupportRepositoryImpl,
    private var mainRepositoryImpl: MainRepositoryImpl
) : BaseViewModel() {

    private var liftingId: Int = 0
    private var _supportData = MutableLiveData<MutableList<Support>>()
    val supportData: LiveData<MutableList<Support>>
        get() = _supportData



    fun getSupports(id: Int) {
        liftingId = id
        ioThread.launch {
            val supports = supportRepository.getSupport(liftingId)
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
}