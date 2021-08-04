package com.smartcity.client.di.main

import android.content.SharedPreferences
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.persistence.AccountPropertiesDao
import com.smartcity.client.persistence.AppDatabase
import com.smartcity.client.persistence.BlogPostDao
import com.smartcity.client.repository.main.AccountRepositoryImpl
import com.smartcity.client.repository.main.ProductRepositoryImpl
import com.smartcity.client.repository.main.CartRepositoryImpl
import com.smartcity.client.repository.main.FlashRepositoryImpl


import com.smartcity.client.session.SessionManager
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.FlowPreview
import retrofit2.Retrofit
import javax.inject.Named

@FlowPreview
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
    ): AccountRepositoryImpl {
        return AccountRepositoryImpl(openApiMainService, accountPropertiesDao, sessionManager)
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
    fun provideProductRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): ProductRepositoryImpl {
        return ProductRepositoryImpl(openApiMainService, blogPostDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideCartRepository(
        openApiMainService: OpenApiMainService,
        blogPostDao: BlogPostDao,
        sessionManager: SessionManager
    ): CartRepositoryImpl {
        return CartRepositoryImpl(openApiMainService, blogPostDao, sessionManager)
    }

    @JvmStatic
    @MainScope
    @Provides
    fun provideFlashRepository(
        openApiMainService: OpenApiMainService,
        sessionManager: SessionManager
    ): FlashRepositoryImpl {
        return FlashRepositoryImpl(openApiMainService, sessionManager)
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

















