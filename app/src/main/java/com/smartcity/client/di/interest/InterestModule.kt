package com.smartcity.client.di.interest

import com.smartcity.client.api.interest.OpenApiInterestService
import com.smartcity.client.persistence.AuthTokenDao
import com.smartcity.client.repository.interest.InterestRepository
import com.smartcity.client.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

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
        openApiInterestService: OpenApiInterestService
    ): InterestRepository {
        return InterestRepository(
            authTokenDao,
            openApiInterestService,
            sessionManager
        )
    }


}
