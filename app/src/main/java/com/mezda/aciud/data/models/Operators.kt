package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName
import javax.inject.Named

@Suppress("SpellCheckingInspection")
data class Operators(
    @SerializedName("Idoperador")
    var operatorId: Int? = null,
    @SerializedName("Idresponsable")
    var supervisorId: Int? = null,
    @SerializedName("IAppID")
    var appId: String? = null,
    @SerializedName("IAppNombre")
    var name: String? = null,
    @SerializedName("IAppLogin")
    var user: String? = null,
    @SerializedName("IAppPwd")
    var password: String? = null,
    @SerializedName("NombreResponsable")
    var supervisorName: String? = null,
    @SerializedName("IAppActivo")
    var active: Boolean? = null,
    @SerializedName("IAppAdmin")
    var isAdmin: Boolean? = null,
    @SerializedName("IAppCaptura")
    var allowCapture: Boolean? = null,
    @SerializedName("IAppModifica")
    var allowModification: Boolean? = null,
    @SerializedName("IAppMobil")
    var allowLogin: Boolean? = null
)
/*
*
{
    "Idoperador": 1,
    "Idresponsable": 2,
    "IAppID": 3,
    "IAppNombre": "sample string 4",
    "IAppLogin": "sample string 5",
    "IAppPwd": "sample string 6",
    "NombreResponsable": "sample string 7",
    "IAppActivo": true,
    "IAppAdmin": true,
    "IAppCaptura": true,
    "IAppModifica": true,
    "IAppMobil": true
  }
* */