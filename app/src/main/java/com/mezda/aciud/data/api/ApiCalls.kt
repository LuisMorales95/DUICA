package com.mezda.aciud.data.api

import com.mezda.aciud.data.models.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiCalls {

    @GET("api/Operadores")
    suspend fun getOperators(
        @Query("pwdApp") password: String
    ): Response<List<Operators>>

    @POST("api/Operadores")
    suspend fun postOperator(
        @Query("pwdApp") password: String,
        @Body body: RequestBody
    ): Response<Int>

    @GET("api/ResponsableSeccion")
    suspend fun getSupervisors(
        @Query("pwdApp") password: String
    ): Response<List<Supervisor>>

    @GET("api/Consultas/GetLocalidad")
    suspend fun getLocalities(
        @Query("pwdApp") password: String
    ): Response<List<Locality>>

    @GET("api/Consultas/GetColonia")
    suspend fun getSuburbs(
        @Query("pwdApp") password: String,
        @Query("IDLOCALIDAD") idLocality: Int
    ): Response<List<Suburb>>

    @POST("api/Levantamiento")
    suspend fun sendLifting(
        @Query("pwdApp") password: String,
        @Body body: RequestBody
    ): Response<Int>

    @PUT("api/Levantamiento")
    suspend fun updateLifting(
        @Query("pwdApp") password: String,
        @Body body: RequestBody
    ): Response<Int>

    @GET("api/Consultas/GetLevantamiento")
    suspend fun getLifting(
        @Query("pwdApp") password: String,
        @Query("idcolonia") suburb: String?,
        @Query("idresponsable") idResponsible: Int,
        @Query("idoperador") idOperator: Int
    ): Response<List<LiftingInfo>>

    @GET("api/Consultas/GetSeccion")
    suspend fun getSection(
        @Query("pwdApp") password: String,
        @Query("idseccion") idSection: String = "null",
        @Query("seccion") section: String = "null",
    ): Response<List<Section>>

    @GET("api/Consultas/GetSeccionByColonia")
    suspend fun getSectionBySuburb(
        @Query("pwdApp") password: String,
        @Query("idseccion") idSection: String = "null",
        @Query("idcolonia") idSuburb: String,
    ) : Response<List<Section>>

    @GET("api/Consultas/GetProfesion")
    suspend fun getProfession(
        @Query("pwdApp") password: String
    ): Response<List<Profession>>


    @GET("api/Consultas/GetTipoApoyo")
    suspend fun getSupportType(
        @Query("pwdApp") password: String
    ): Response<List<SupportTypes>>

    @GET("api/Consultas/GetBandera")
    suspend fun getFlags(
        @Query("pwdApp") password: String
    ): Response<List<Flag>>
}