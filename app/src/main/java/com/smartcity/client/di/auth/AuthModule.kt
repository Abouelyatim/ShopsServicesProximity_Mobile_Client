package com.smartcity.client.di.auth

import android.content.SharedPreferences
import com.smartcity.client.api.auth.OpenApiAuthService
import com.smartcity.client.persistence.AccountPropertiesDao
import com.smartcity.client.persistence.AuthTokenDao
import com.smartcity.client.repository.auth.AuthRepository
import com.smartcity.client.repository.auth.AuthRepositoryImpl
import com.smartcity.client.session.SessionManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit

@FlowPreview
@Module
object AuthModule{

    @JvmStatic
    @AuthScope
    @Provides
    fun provideApiAuthService(retrofitBuilder: Retrofit.Builder): OpenApiAuthService {
        return retrofitBuilder
            .build()
            .create(OpenApiAuthService::class.java)
    }

    @JvmStatic
    @AuthScope
    @Provides
    fun provideAuthRepository(
        sessionManager: SessionManager,
        authTokenDao: AuthTokenDao,
        accountPropertiesDao: AccountPropertiesDao,
        openApiAuthService: OpenApiAuthService,
        preferences: SharedPreferences,
        editor: SharedPreferences.Editor
        ): AuthRepository {
        return AuthRepositoryImpl(
            authTokenDao,
            accountPropertiesDao,
            openApiAuthService,
            sessionManager,
            preferences,
            editor
        )
    }


}









