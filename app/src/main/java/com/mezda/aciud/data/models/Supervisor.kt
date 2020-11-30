package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName

data class Supervisor (
    @SerializedName("Idresponsables")
    var supervisorId: Int? = null,
    @SerializedName("Nombre")
    var name: String? = null,
    @SerializedName("Idseccion")
    var sectionId: Int? = null
)

/*
* [
  {
    "Idresponsables": 2,
    "Nombre": "ABRHAN ANTONIO MATIAS",
    "Idseccion": 2
  },
  {
    "Idresponsables": 4,
    "Nombre": "MOPE ROBLEZ CRUZ",
    "Idseccion": 4
  },
  {
    "Idresponsables": 3,
    "Nombre": "ROBERTA CRUZ PERES",
    "Idseccion": 3
  }
]*/