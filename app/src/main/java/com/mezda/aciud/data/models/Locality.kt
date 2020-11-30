package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName

data class Locality(
    @SerializedName("IDLOCALIDAD")
    var idLocality: Int? = null,
    @SerializedName("LOCALIDADName")
    var nameLocality: String? = null
)
/*
*
[
  {
    "IDLOCALIDAD": 1,
    "LOCALIDADName": "sample string 2"
  },
  {
    "IDLOCALIDAD": 1,
    "LOCALIDADName": "sample string 2"
  }
]
* */