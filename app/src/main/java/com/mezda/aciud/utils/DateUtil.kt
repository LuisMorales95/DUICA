@file:Suppress("DEPRECATION")

package com.mezda.aciud.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    val simpleDate = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

    fun simpleFormat(dateString: String): String {
        return simpleDate.format(Date.parse(dateString)).toString()
    }
}