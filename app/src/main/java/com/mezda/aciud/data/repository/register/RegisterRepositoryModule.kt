package com.mezda.aciud.data.repository.register

import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.preference.Preference
import com.mezda.aciud.data.repository.login.LoginRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class RegisterRepositoryModule {

    @ActivityScoped
    @Provides
    fun providesRegisterRepository(apiCalls: ApiCalls, preference: Preference): RegisterRepositoryImpl {
        return RegisterRepositoryImpl(apiCalls, preference)
    }
}