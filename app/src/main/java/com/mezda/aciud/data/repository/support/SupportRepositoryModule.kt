package com.mezda.aciud.data.repository.support

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
class SupportRepositoryModule {

    @ActivityScoped
    @Provides
    fun providesUserRepository(apiCalls: ApiCalls, preference: Preference): SupportRepositoryImpl {
        return SupportRepositoryImpl(apiCalls, preference)
    }
}