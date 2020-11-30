package com.mezda.aciud.data.repository.lifting

import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.Locality
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.data.models.Suburb
import com.mezda.aciud.data.models.Supervisor
import com.mezda.aciud.data.preference.Preference
import retrofit2.Response
import retrofit2.http.Query

class LiftingRepositoryImpl (
    private val apiCalls: ApiCalls,
    private val preference: Preference
) {
    suspend fun getSupervisors( ): Response<List<Supervisor>> {
        return apiCalls.getSupervisors(preference.pwdApp())
    }

    suspend fun getLocalities(): Response<List<Locality>> {
        return apiCalls.getLocalities(preference.pwdApp())
    }

    suspend fun getSuburbs(idLocality: Int): Response<List<Suburb>> {
        return apiCalls.getSuburbs(preference.pwdApp(),idLocality)
    }
}