package com.smartcity.client.di.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.di.auth.keys.MainViewModelKey
import com.smartcity.client.ui.main.account.viewmodel.AccountViewModel
import com.smartcity.client.ui.main.product.viewmodel.ProductViewModel
import com.smartcity.client.ui.main.cart.viewmodel.CartViewModel
import com.smartcity.client.ui.main.flash_notification.viewmodel.FlashViewModel

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
    @MainViewModelKey(AccountViewModel::class)
    abstract fun bindAccountViewModel(accoutViewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(ProductViewModel::class)
    abstract fun bindProductViewModel(productViewModel: ProductViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(CartViewModel::class)
    abstract fun bindCartViewModel(cartViewModel: CartViewModel): ViewModel

    @Binds
    @IntoMap
    @MainViewModelKey(FlashViewModel::class)
    abstract fun bindFlashViewModel(flashViewModel: FlashViewModel): ViewModel
}








