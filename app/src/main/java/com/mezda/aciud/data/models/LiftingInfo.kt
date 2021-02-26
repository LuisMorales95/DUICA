package com.mezda.aciud.data.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LiftingInfo(
    @SerializedName("Nombre")
    var name: String? = null,
    @SerializedName("ApellidoPaterno")
    var paternal_surname: String? = null,
    @SerializedName("ApellinoMaterno")
    var maternal_surname: String? = null,
    @SerializedName("Telefono")
    var phone: String? = null,
    @SerializedName("Calle")
    var street: String? = null,
    @SerializedName("Numero")
    var number: String? = null,
    @SerializedName("Latitude")
    var latitude: String? = null,
    @SerializedName("Longitude")
    var longitude: String? = null,
    @SerializedName("Fecha")
    var date: String? = null,
    @SerializedName("Idlevantamiento")
    var idLifting: Int? = 0,
    @SerializedName("Idcolonia")
    var idSuburb: Int? = null,
    @SerializedName("Idresponsable")
    var idSupervisor: Int? = null,
    @SerializedName("Idoperador")
    var idOperator: Int? = null,
    @SerializedName("Seccion")
    var section: String? = null,
    @SerializedName("Idseccion")
    var sectionId: Int? = null,

    @SerializedName("Idprofesion")
    var professionId: Int? = null,
    @SerializedName("Observaciones")
    var observations: String? = null,
    @SerializedName("Simpatizante")
    var sympathizer: Int? = null,
    @SerializedName("Idbandera")
    var idFlag: Int? = null,
    @SerializedName("Imagen")
    var image: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
    }

    fun fullName(): String {
        return "$name $paternal_surname $maternal_surname"
    }

    companion object CREATOR : Parcelable.Creator<LiftingInfo> {
        override fun createFromParcel(parcel: Parcel): LiftingInfo {
            return LiftingInfo(parcel)
        }

        override fun newArray(size: Int): Array<LiftingInfo?> {
            return arrayOfNulls(size)
        }
    }


}