package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName
import javax.inject.Named

@Suppress("SpellCheckingInspection")
data class Operators(
    @SerializedName("Idoperador")
    var operatorId: Int? = null,
    @SerializedName("Idresponsable")
    var supervisorId: Int? = null,
    @SerializedName("Usuario")
    var user: String? = null,
    @SerializedName("Nombre")
    var name: String? = null
)
/*
*
[
  {
    "Idoperador": 1,
    "Idresponsable": 2,
    "Usuario": "sample string 3",
    "Nombre": "sample string 4"
  },
  {
    "Idoperador": 1,
    "Idresponsable": 2,
    "Usuario": "sample string 3",
    "Nombre": "sample string 4"
  }
]
* */