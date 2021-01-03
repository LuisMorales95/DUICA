package com.mezda.aciud.ui.lifting_flow

import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mezda.aciud.data.repository.lifting.LiftingRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import java.lang.IllegalArgumentException

class LiftingFlowViewModelProvider (
    private val mainRepositoryImpl: MainRepositoryImpl,
    private val liftingRepositoryImpl: LiftingRepositoryImpl
): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LiftingFlowViewModel::class.java)){
            return LiftingFlowViewModel(mainRepositoryImpl, liftingRepositoryImpl) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}
