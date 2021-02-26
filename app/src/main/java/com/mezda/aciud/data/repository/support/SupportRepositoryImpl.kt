package com.mezda.aciud.data.repository.support

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.mezda.aciud.ACIUDApp
import com.mezda.aciud.R
import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.models.Flag
import com.mezda.aciud.data.models.LiftingInfo
import com.mezda.aciud.data.models.Support
import com.mezda.aciud.data.models.SupportTypes
import com.mezda.aciud.data.preference.Preference
import com.mezda.aciud.ui.lifting_flow.user_info.UserInfoFragment
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File
import java.io.IOException

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

    suspend fun sendSupport(support: Support): Response<Int> {
        val body = Gson().toJson(support).toRequestBody(ACIUDApp.mediaType)
        return apiCalls.sendSupport(preference.pwdApp(), body)
    }

    suspend fun deleteSupport(supportId: Int): Response<Int>{
        return apiCalls.deleteSupport(preference.pwdApp(), supportId.toString())
    }
}
