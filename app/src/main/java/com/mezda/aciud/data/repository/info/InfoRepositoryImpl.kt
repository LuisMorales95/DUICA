package com.mezda.aciud.data.repository.info

import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.*
import com.mezda.aciud.data.preference.Preference
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

class InfoRepositoryImpl(
    private val apiCalls: ApiCalls,
    private val preference: Preference
) {

    suspend fun getLiftingInfo(liftingId: Int): Response<List<LiftingInfoVisualization>> {
        return apiCalls.getLiftingInfo(preference.pwdApp(),liftingId.toString())
    }
}