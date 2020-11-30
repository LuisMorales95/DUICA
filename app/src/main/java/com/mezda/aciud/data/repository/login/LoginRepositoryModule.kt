package com.mezda.aciud.data.repository.login

import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.preference.Preference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class LoginRepositoryModule {

    @ActivityScoped
    @Provides
    fun providesLoginRepository(apiCalls: ApiCalls, preference: Preference): LoginRepositoryImpl {
        return LoginRepositoryImpl(apiCalls, preference)
    }
}