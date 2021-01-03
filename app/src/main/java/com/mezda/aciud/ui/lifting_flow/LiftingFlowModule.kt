package com.mezda.aciud.ui.lifting_flow

import com.mezda.aciud.data.repository.lifting.LiftingRepositoryImpl
import com.mezda.aciud.data.repository.main.MainRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
class LiftingFlowModule {

    @ActivityScoped
    @Provides
    fun providesLiftingFlowModule(
        mainRepositoryImpl: MainRepositoryImpl,
        liftingRepositoryImpl: LiftingRepositoryImpl
    ) = LiftingFlowViewModelProvider(mainRepositoryImpl, liftingRepositoryImpl)
}