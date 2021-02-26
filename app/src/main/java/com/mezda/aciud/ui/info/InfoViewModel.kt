package com.mezda.aciud.ui.info

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.mezda.aciud.data.models.LiftingInfoVisualization
import com.mezda.aciud.data.repository.info.InfoRepositoryImpl
import com.mezda.aciud.ui.BaseViewModel
import com.mezda.aciud.utils.ValueWrapper
import kotlinx.coroutines.launch

class InfoViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private var infoRepositoryImpl: InfoRepositoryImpl
) : BaseViewModel() {

    private val _loading = MutableLiveData<ValueWrapper<Boolean>>()
    val loading: LiveData<ValueWrapper<Boolean>>
        get() = _loading

    private var _liftingInfo = MutableLiveData<List<LiftingInfoVisualization>>()
    val liftingInfo : LiveData<List<LiftingInfoVisualization>>
        get() = _liftingInfo


    fun getLiftingInfo(liftingId: Int) {
        ioThread.launch {
            _loading.postValue(ValueWrapper(true))
            val response = infoRepositoryImpl.getLiftingInfo(liftingId)
            _loading.postValue(ValueWrapper(false))
            if (response.isSuccessful) {
                _liftingInfo.postValue(response.body())
            }
        }
    }
}