package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName

data class SupportTypes(
    @SerializedName("Idtapoyo")
    var id: Int = 0,
    @SerializedName("Tipoapoyo")
    var supportType: String = ""
)