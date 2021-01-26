package com.mezda.aciud.data

import com.google.gson.Gson
import com.mezda.aciud.data.api.ApiCalls
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ActivityScoped
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(ActivityComponent::class)
class RetrofitModule {

    companion object {
        const val baseUrl = "http://prep.cccmexctz.com.mx/"
        const val testingUrl = "http://prepr1.cccmexctz.com.mx/"
    }

    @Provides
    @ActivityScoped
    fun providesOkHttpClient(): OkHttpClient {
        val headerInterceptor = HttpLoggingInterceptor()
        headerInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
        val bodyInterceptor = HttpLoggingInterceptor()
        bodyInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
                .addInterceptor(headerInterceptor)
                .addInterceptor(bodyInterceptor)
                .connectTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build()
    }

    @Provides
    @ActivityScoped
    fun providesRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(testingUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    @ActivityScoped
    fun providesApiCalls(retrofit: Retrofit): ApiCalls {
        return retrofit.create(ApiCalls::class.java)
    }


}