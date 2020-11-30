package com.mezda.aciud.data.repository.lifting

import com.mezda.aciud.data.api.ApiCalls
import com.mezda.aciud.data.preference.Preference
import com.mezda.aciud.data.repository.register.RegisterRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class LiftingRepositoryModule {

    @ActivityScoped
    @Provides
    fun providesLiftingRepository(apiCalls: ApiCalls, preference: Preference): LiftingRepositoryImpl {
        return LiftingRepositoryImpl(apiCalls, preference)
    }
}