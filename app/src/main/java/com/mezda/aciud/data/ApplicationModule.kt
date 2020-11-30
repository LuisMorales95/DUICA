package com.mezda.aciud.data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.createDataStore
import com.mezda.aciud.data.preference.Preference
import com.mezda.aciud.data.preference.PreferenceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
class ApplicationModule {

    @Provides
    @ActivityScoped
    fun providesContext(application: Application): Context {
        return application
    }

    @Provides
    @ActivityScoped
    fun providesDataStore(context: Context): DataStore<Preferences> {
        return context.createDataStore(
            name = "Preferences"
        )
    }

    @Provides
    @ActivityScoped
    fun providesPreference(context: Context, dataStore: DataStore<Preferences>): Preference {
        return PreferenceImpl(context, dataStore)
    }
}