package com.mezda.aciud.data.repository.search

import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.*
import com.mezda.aciud.data.preference.Preference
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

class SearchRepositoryImpl(
    private val apiCalls: ApiCalls,
    private val preference: Preference
) {

    suspend fun getLocalities(): Response<List<Locality>> {
        return apiCalls.getLocalities(preference.pwdApp())
    }

    suspend fun getSuburbs(idLocality: Int): Response<List<Suburb>> {
        return apiCalls.getSuburbs(preference.pwdApp(),idLocality)
    }

    suspend fun getLifting(
        suburb: String?, idResponsible: Int, idOperator: Int
    ): Response<List<LiftingInfo>> {
        return apiCalls.getLifting(password = preference.pwdApp(), idResponsible = idResponsible, idOperator =  idOperator, suburb = suburb)
    }

    suspend fun getOperators(): Response<List<Operators>> {
        return apiCalls.getOperators(preference.pwdApp())
    }

}