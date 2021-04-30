package com.smartcity.client.di.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.di.auth.keys.MainViewModelKey
import com.smartcity.client.ui.main.store.StoreViewModel
import com.smartcity.client.ui.main.blog.viewmodel.ProductViewModel
import com.smartcity.client.ui.main.cart.viewmodel.CartViewModel

import com.smartcity.client.viewmodels.MainViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: MainViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @MainViewModelKey(StoreViewModel::class)
    abstract fun bindAccountViewModel(accoutViewModel: StoreViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(ProductViewModel::class)
    abstract fun bindBlogViewModel(productViewModel: ProductViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(CartViewModel::class)
    abstract fun bindCreateBlogViewModel(cartViewModel: CartViewModel): ViewModel
}








