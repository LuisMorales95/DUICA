package com.mezda.aciud.data.models

data class OccupationInfo(
    var professionId: Int? = 0,
    var profession: String? = "",
    var supportTypesId: Int? = 0,
    var supportTypes: String? = "",
    var observation: String? = ""
)
