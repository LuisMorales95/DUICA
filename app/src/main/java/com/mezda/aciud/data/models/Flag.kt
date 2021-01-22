package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName

data class Flag(
    @SerializedName("Idbandera")
    var id: Int? = 0,
    @SerializedName("Bandera")
    var flag: String? = ""
)
