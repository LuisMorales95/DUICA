package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName

data class Support(
    @SerializedName("IdApoyoMultiple")
    var idSupport: Int? = null,
    @SerializedName("IdLevantamiento")
    var idLifting: Int? = null,
    @SerializedName("IdTipoApoyo")
    var idSupportType: Int? = null,
    @SerializedName("Tipoapoyo")
    var supportTypeName: String? = null,
    @SerializedName("Imagen")
    var image: String? = null,
    @SerializedName("Fecha")
    var date: String? = null
)
/*"IdApoyoMultiple": 1,
  "IdLevantamiento": 2,
  "IdTipoApoyo": 3,
  "Imagen": "sample string 4",
  "Nombre": "sample string 5",
  "ApellidoPaterno": "sample string 6",
  "ApellinoMaterno": "sample string 7",
  "Tipoapoyo": "sample string 8",
  "Fecha": "2021-02-21T18:52:25.6397137-06:00"*/