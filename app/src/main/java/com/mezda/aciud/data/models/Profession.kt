package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName

data class Profession(
    @SerializedName("Idprofesion")
    var id: Int = 0,
    @SerializedName("Prefesion")
    var profession: String = ""
)