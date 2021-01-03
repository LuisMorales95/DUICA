package com.mezda.aciud.data.models

import android.graphics.Bitmap

data class UserInfo(
    var name: String = "",
    var paternal_last_name: String = "",
    var maternal_last_name: String = "",
    var phone_number: String = "",
    var picture: Bitmap? = null,
    var picture_url: String? = null,
    var picture_uri: String? = null,
    var picture_encoded: String? = null
)
