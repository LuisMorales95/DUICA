package com.mezda.aciud.data.api

import com.mezda.aciud.data.models.Locality
import com.mezda.aciud.data.models.Operators
import com.mezda.aciud.data.models.Suburb
import com.mezda.aciud.data.models.Supervisor
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiCalls {

    @GET("api/Operadores")
    suspend fun getOperators(@Query("pwdApp") password: String): Response<List<Operators>>

    @POST("api/Operadores")
    suspend fun postOperator(
        @Query("pwdApp") password: String,
        @Body body: RequestBody
    ): Response<Int>

    @GET("api/ResponsableSeccion")
    suspend fun getSupervisors(@Query("pwdApp") password: String): Response<List<Supervisor>>

    @GET("api/Consultas/GetLocalidad")
    suspend fun getLocalities(@Query("pwdApp") password: String): Response<List<Locality>>


    @GET("api/Consultas/GetColonia")
    suspend fun getSuburbs(
        @Query("pwdApp") password: String,
        @Query("IDLOCALIDAD") idLocality: Int
    ): Response<List<Suburb>>

}