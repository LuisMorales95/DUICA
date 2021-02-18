package com.mezda.aciud.data.repository.main

import androidx.lifecycle.MutableLiveData
import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.data.preference.Preference
import com.mezda.aciud.utils.ValueWrapper
import retrofit2.Response

class MainRepositoryImpl() {

    val liveOperator = MutableLiveData<Operators>()


}