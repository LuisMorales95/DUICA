package com.mezda.aciud.data.models

data class DirectionInfo(
    var street: String? = "",
    var street_number: Int? = 0,
    var locality: Int? = 0,
    var section: Int? = 0,
    var suburb: Int? = 0
)