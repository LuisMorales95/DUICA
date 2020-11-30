package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName

data class Suburb(
    @SerializedName("IDCOLONIA")
    var idSuburb: Int? = null,
    @SerializedName("IDLOCALIDAD")
    var idLocality: Int? = null,
    @SerializedName("COLONIAName")
    var nameSuburb: String? = null
)

/*[
  {
    "IDCOLONIA": 1,
    "IDLOCALIDAD": 2,
    "COLONIAName": "sample string 3"
  },
  {
    "IDCOLONIA": 1,
    "IDLOCALIDAD": 2,
    "COLONIAName": "sample string 3"
  }
]*/