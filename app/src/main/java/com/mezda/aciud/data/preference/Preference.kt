package com.mezda.aciud.data.preference

import kotlinx.coroutines.flow.Flow

interface Preference {
    companion object {
    }

    fun getString(key: String): Flow<String>
    fun getInt(key: String): Flow<Int>

    fun pwdApp(): String
}