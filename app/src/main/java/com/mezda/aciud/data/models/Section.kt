package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName

data class Section(
        @SerializedName("Idseccion")
        var idSection : Int = 0,
        @SerializedName("Seccion")
        var section: String = "",
        @SerializedName("IdMun")
        var idMunicipality: Int = 0,

        @SerializedName("IdRel")
        var idRel: Int = 0,

        @SerializedName("Idcolonia")
        var idColonia: Int = 0,

        @SerializedName("COLONIA")
        var suburb: String = ""
)