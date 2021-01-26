package com.mezda.aciud.data.models

data class DirectionInfo(
    var street: String? = "",
    var street_number: Int? = 0,
    var locality: String? = "",
    var localityId: Int? = 0,
    var section: String? = "",
    var sectionId: Int? = 0,
    var suburb: String? = "",
    var suburbId: Int? = 0
)