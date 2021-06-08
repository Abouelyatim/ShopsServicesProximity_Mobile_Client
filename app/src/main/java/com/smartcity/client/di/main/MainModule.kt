package com.smartcity.client.di.main

import android.content.SharedPreferences
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.persistence.AccountPropertiesDao
import com.smartcity.client.persistence.AppDatabase
import com.smartcity.client.persistence.BlogPostDao
import com.smartcity.client.repository.main.AccountRepository
import com.smartcity.client.repository.main.BlogRepository
import com.smartcity.client.repository.main.CartRepository


import com.smartcity.client.session.SessionManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
object MainModule {

    @JvmStatic
    @MainScope
    @Provides
    fun provideOpenApiMainService(retrofitBuilder: Retrofit.Builder): OpenApiMainService {
        return retrofitBuilder
            .build()
            .create(OpenApiMainService::class.java)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideAccountRepository(
        openApiMainService: OpenApiMainService,
        accountPropertiesDao: AccountPropertiesDao,
        sessionManager: SessionManager
    ): AccountRepository {
        return AccountRepository(openApiMainService, accountPropertiesDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogPostDao(db: AppDatabase): BlogPostDao {
        return db.getBlogPostDao()
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideBlogRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): BlogRepository {
        return BlogRepository(openApiMainService, blogPostDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideCartRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): CartRepository {
        return CartRepository(openApiMainService, blogPostDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("GetSharedPreferences")
    fun provideSharedPreferences(
        sharedPreferences: SharedPreferences
    ): SharedPreferences {
        return sharedPreferences
    }
}

















