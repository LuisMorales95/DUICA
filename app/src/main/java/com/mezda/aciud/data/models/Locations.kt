package com.mezda.aciud.data.models

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Locations(
    var list: MutableList<LiftingInfo> = mutableListOf(),
    var singleLocation: Boolean
): Serializable