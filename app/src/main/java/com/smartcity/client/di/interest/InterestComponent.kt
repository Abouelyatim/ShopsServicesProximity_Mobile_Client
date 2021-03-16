package com.smartcity.client.di.interest

import com.smartcity.client.ui.interest.InterestActivity
import dagger.Subcomponent


@InterestScope
@Subcomponent(
    modules = [
        InterestModule::class,
        InterestViewModelModule::class,
        InterestFragmentsModule::class
    ])
interface InterestComponent {

    @Subcomponent.Factory
    interface Factory{

        fun create(): InterestComponent
    }

    fun inject(interestActivity: InterestActivity)

}
