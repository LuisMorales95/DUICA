package com.mezda.aciud.ui.support

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mezda.aciud.data.repository.lifting.LiftingRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import com.mezda.aciud.data.repository.support.SupportRepositoryImpl
import java.lang.IllegalArgumentException

class SupportFlowViewModelProvider (
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val supportRepositoryImpl: SupportRepositoryImpl
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SupportViewModel::class.java)){
            return SupportViewModel(supportRepositoryImpl,mainRepositoryImpl) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
