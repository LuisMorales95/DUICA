package com.mezda.aciud.data.repository.lifting

import com.google.gson.Gson
import com.mezda.aciud.ACIUDApp
import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.*
import com.mezda.aciud.data.preference.Preference
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

class LiftingRepositoryImpl(
    private val apiCalls: ApiCalls,
    private val preference: Preference
) {

    suspend fun getSupervisors(): Response<List<Supervisor>> {
        return apiCalls.getSupervisors(preference.pwdApp())
    }

    suspend fun getLocalities(): Response<List<Locality>> {
        return apiCalls.getLocalities(preference.pwdApp())
    }

    suspend fun getSuburbs(idLocality: Int): Response<List<Suburb>> {
        return apiCalls.getSuburbs(preference.pwdApp(), idLocality)
    }

    suspend fun sendLifting(liftingInfo: LiftingInfo): Response<Int> {
        val body = Gson().toJson(liftingInfo).toRequestBody(ACIUDApp.mediaType)
        return apiCalls.sendLifting(preference.pwdApp(), body)
    }

    suspend fun getSection(): List<Section> {
        val response = apiCalls.getSection(preference.pwdApp())
        return if (response.isSuccessful) {
            response.body() ?: listOf()
        } else listOf()
    }

    suspend fun getProfession(): List<Profession> {
        val response = apiCalls.getProfession(preference.pwdApp())
        return if (response.isSuccessful) {
            response.body() ?: listOf()
        } else listOf()
    }

    suspend fun getSupportType(): List<SupportTypes>{
        val response = apiCalls.getSupportType(preference.pwdApp())
        return if (response.isSuccessful) {
            response.body() ?: listOf()
        } else listOf()
    }

    suspend fun getFlags(): List<Flag> {
        val response = apiCalls.getFlags(preference.pwdApp())
        return if (response.isSuccessful) {
            response.body() ?: listOf()
        } else {
            listOf()
        }
    }
}