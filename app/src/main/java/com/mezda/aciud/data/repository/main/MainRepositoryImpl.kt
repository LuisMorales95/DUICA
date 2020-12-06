package com.mezda.aciud.data.repository.main

import androidx.lifecycle.MutableLiveData
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.utils.ValueWrapper

class MainRepositoryImpl {

    val liveOperator = MutableLiveData<Operators>()
}