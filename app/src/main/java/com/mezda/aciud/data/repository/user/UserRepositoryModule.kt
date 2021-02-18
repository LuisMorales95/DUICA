package com.mezda.aciud.data.repository.user

import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.preference.Preference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(ActivityComponent::class)
class UserRepositoryModule {

    @ActivityScoped
    @Provides
    fun providesUserRepository(apiCalls: ApiCalls, preference: Preference): UserRepositoryImpl {
        return UserRepositoryImpl(apiCalls, preference)
    }
}