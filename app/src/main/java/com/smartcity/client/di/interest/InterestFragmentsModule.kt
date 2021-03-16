package com.smartcity.client.di.interest

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.fragments.interest.InterestFragmentFactory
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object InterestFragmentsModule {

    @JvmStatic
    @InterestScope
    @Provides
    @Named("InterestFragmentFactory")
    fun provideFragmentFactory(
        viewModelFactory: ViewModelProvider.Factory,
        requestManager: RequestManager
    ): FragmentFactory {
        return InterestFragmentFactory(
            viewModelFactory,
            requestManager
        )
    }


}