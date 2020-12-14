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
    @SerializedName("Idtapoyo")
    var supportTypeId: Int? = null,
    @SerializedName("Observaciones")
    var observations: String? = null,
    @SerializedName("Simpatizante")
    var sympathizer: Boolean? = null,
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
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(paternal_surname)
        parcel.writeString(maternal_surname)
        parcel.writeString(phone)
        parcel.writeString(street)
        parcel.writeString(number)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(date)
        parcel.writeValue(idLifting)
        parcel.writeValue(idSuburb)
        parcel.writeValue(idSupervisor)
        parcel.writeValue(idOperator)
        parcel.writeString(section)
        parcel.writeValue(sectionId)
        parcel.writeValue(professionId)
        parcel.writeValue(supportTypeId)
        parcel.writeString(observations)
        parcel.writeValue(sympathizer)
        parcel.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
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

/*
 "Seccion": "sample string 14",
  "Idseccion": 15

{
  "Idlevantamiento": 1,
  "Idcolonia": 2,
  "Idresponsable": 3,
  "Idoperador": 4,
  "Nombre": "sample string 5",
  "ApellidoPaterno": "sample string 6",
  "ApellinoMaterno": "sample string 7",
  "Telefono": "sample string 8",
  "Calle": "sample string 9",
  "Numero": "sample string 10",
  "Latitude": "sample string 11",
  "Longitude": "sample string 12",
  "Fecha": "2020-12-01T20:15:06.3184436-06:00"
}
*/