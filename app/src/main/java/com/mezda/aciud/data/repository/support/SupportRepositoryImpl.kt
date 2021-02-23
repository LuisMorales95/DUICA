package com.mezda.aciud.data.repository.support

import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.Flag
import com.mezda.aciud.data.models.Support
import com.mezda.aciud.data.models.SupportTypes
import com.mezda.aciud.data.preference.Preference

class SupportRepositoryImpl(
    private val apiCalls: ApiCalls,
    private val preference: Preference
) {
    suspend fun getSupport(liftingId: Int): List<Support> {
        val response = apiCalls.getSupports(preference.pwdApp(), liftingId.toString())
        return if (response.isSuccessful) {
            response.body() ?: listOf()
        } else {
            listOf()
        }
    }

    suspend fun getSupportType(): List<SupportTypes>{
        val response = apiCalls.getSupportType(preference.pwdApp())
        return if (response.isSuccessful) {
            response.body() ?: listOf()
        } else listOf()
    }
}
