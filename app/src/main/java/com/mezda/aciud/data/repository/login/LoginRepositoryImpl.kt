package com.mezda.aciud.data.repository.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.data.preference.Preference
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class LoginRepositoryImpl (
    var apiCalls: ApiCalls,
    val preference: Preference
) {

    suspend fun getOperators(): Response<List<Operators>> {
        return apiCalls.getOperators(preference.pwdApp())
    }
}