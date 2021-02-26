package com.mezda.aciud.data.models

import com.google.gson.annotations.SerializedName

class LiftingInfoVisualization(
    @SerializedName("Idlevantamiento")
    var liftingId: Int = 0,
    @SerializedName("Colonia")
    var colony: String = "",
    @SerializedName("Responsable")
    var responsable: String = "",
    @SerializedName("Operador")
    var operator: String = "",
    @SerializedName("Profesion")
    var profession: String = "",
    @SerializedName("Bandera")
    var flag: String = "",
    @SerializedName("Seccion")
    var seccion: Int = 0,
    @SerializedName("Nombre")
    var name: String = "",
    @SerializedName("ApellidoPaterno")
    var parental: String = "",
    @SerializedName("ApellinoMaterno")
    var maternal: String = "",
    @SerializedName("Telefono")
    var phone: String = "",
    @SerializedName("Calle")
    var street: String = "",
    @SerializedName("Numero")
    var number: String = "",
    @SerializedName("Latitude")
    var latitude: String = "",
    @SerializedName("Longitude")
    var longitude: String = "",
    @SerializedName("Simpatizante")
    var simpathizer: String = "",
    @SerializedName("Imagen")
    var image: String = "",
    @SerializedName("Observaciones")
    var observation: String = "",
    @SerializedName("Fecha")
    var date: String = ""
)