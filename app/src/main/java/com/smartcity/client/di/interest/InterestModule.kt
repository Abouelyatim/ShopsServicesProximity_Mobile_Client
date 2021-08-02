package com.smartcity.client.di.interest

import android.content.SharedPreferences
import com.smartcity.client.api.interest.OpenApiInterestService
import com.smartcity.client.persistence.AuthTokenDao
import com.smartcity.client.repository.interest.InterestRepositoryImpl
import com.smartcity.client.session.SessionManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import javax.inject.Named

@FlowPreview
@Module
object InterestModule{

    @JvmStatic
    @InterestScope
    @Provides
    fun provideApiInterestService(retrofitBuilder: Retrofit.Builder): OpenApiInterestService {
        return retrofitBuilder
            .build()
            .create(OpenApiInterestService::class.java)
    }

    @JvmStatic
    @InterestScope
    @Provides
    fun provideInterestRepository(
        authTokenDao: AuthTokenDao,
        sessionManager: SessionManager,
        openApiInterestService: OpenApiInterestService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor
    ): InterestRepositoryImpl {
        return InterestRepositoryImpl(
            authTokenDao,
            openApiInterestService,
            sessionManager,
            preferences,
            editor
        )
    }

    @JvmStatic
    @InterestScope
    @Provides
    @Named("GetSharedPreferences")
    fun provideSharedPreferences(
        sharedPreferences: SharedPreferences
    ): SharedPreferences {
        return sharedPreferences
    }
}
