package com.mezda.aciud.ui.support

import com.mezda.aciud.data.repository.lifting.LiftingRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import com.mezda.aciud.data.repository.support.SupportRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class SupportFlowModule {

    @ActivityScoped
    @Provides
    fun providesLiftingFlowModule(
        mainRepositoryImpl: MainRepositoryImpl,
        supportRepositoryImpl: SupportRepositoryImpl
    ) = SupportFlowViewModelProvider(mainRepositoryImpl, supportRepositoryImpl)
}