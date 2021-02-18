package com.mezda.aciud.data.repository.user

import com.google.gson.Gson
import com.mezda.aciud.ACIUDApp
import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.data.preference.Preference
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

class UserRepositoryImpl(
    private val apiCalls: ApiCalls,
    private val preference: Preference
) {

    suspend fun getOperators(): Response<List<Operators>> {
        return apiCalls.getOperators(preference.pwdApp())
    }

    suspend fun updateOperator(operators: Operators): Response<Int> {
        val body = Gson().toJson(operators).toRequestBody(ACIUDApp.mediaType)
        return apiCalls.updateOperator(preference.pwdApp(), body)
    }
}