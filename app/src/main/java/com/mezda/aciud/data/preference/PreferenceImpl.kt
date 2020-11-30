package com.mezda.aciud.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.preferencesKey
import com.mezda.aciud.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferenceImpl(
    var context: Context,
    var dataStore: DataStore<Preferences>
): Preference {

    override fun getString(key: String): Flow<String> {
        val preference = preferencesKey<String>(key)
        return dataStore.data.map {
            it[preference] ?: ""
        }
    }

    override fun getInt(key: String): Flow<Int> {
        val preference = preferencesKey<Int>(key)
        return dataStore.data.map {
            it[preference] ?: 0
        }
    }

    override fun pwdApp(): String {
        return context.getString(R.string.API_KEY)
    }
}