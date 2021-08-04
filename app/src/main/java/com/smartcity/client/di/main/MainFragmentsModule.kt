package com.smartcity.client.di.main

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.fragments.main.account.AccountFragmentFactory
import com.smartcity.client.fragments.main.product.ProductFragmentFactory
import com.smartcity.client.fragments.main.cart.CartFragmentFactory
import com.smartcity.client.fragments.main.flash_notification.FlashFragmentFactory


import dagger.Module
import dagger.Provides
import javax.inject.Named
@Module
object MainFragmentsModule {

    @JvmStatic
    @MainScope
    @Provides
    @Named("AccountFragmentFactory")
    fun provideAccountFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return AccountFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("ProductFragmentFactory")
    fun provideProductFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return ProductFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("CartFragmentFactory")
    fun provideCartFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return CartFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }

    @JvmStatic
    @MainScope
    @Provides
    @Named("FlashFragmentFactory")
    fun provideFlashFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return FlashFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }
}