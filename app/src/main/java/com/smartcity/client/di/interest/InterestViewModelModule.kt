package com.smartcity.client.di.interest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smartcity.client.di.interest.keys.InterestViewModelKey
import com.smartcity.client.ui.interest.InterestViewModel
import com.smartcity.client.viewmodels.InterestViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class InterestViewModelModule {

    @InterestScope
    @Binds
    abstract fun bindViewModelFactory(factory: InterestViewModelFactory): ViewModelProvider.Factory

    @InterestScope
    @Binds
    @IntoMap
    @InterestViewModelKey(InterestViewModel::class)
    abstract fun bindInterestViewModel(interestViewModel: InterestViewModel): ViewModel

}