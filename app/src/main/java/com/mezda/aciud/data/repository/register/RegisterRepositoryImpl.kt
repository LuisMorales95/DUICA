package com.mezda.aciud.data.repository.register

import com.mezda.aciud.ACIUDApp
import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.data.models.Supervisor
import com.mezda.aciud.data.preference.Preference
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Response

class RegisterRepositoryImpl (
    private val apiCalls: ApiCalls,
    private val preference: Preference
) {

    suspend fun getSupervisors(): Response<List<Supervisor>> {
        return apiCalls.getSupervisors(preference.pwdApp())
    }

    suspend fun postOperator(user: String, name: String, supervisorId: Int): Response<Int> {
        val map = mutableMapOf<String, Any>()
        map["Idoperador"] = 0
        map["Idresponsable"] = supervisorId
        map["Usuario"] = user
        map["Nombre"] = name
        val requestBody = JSONObject(map as Map<*, *>).toString().toRequestBody(ACIUDApp.mediaType)
        return apiCalls.postOperator(preference.pwdApp(),requestBody)
    }
}