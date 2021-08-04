package com.smartcity.client.di.main

import com.smartcity.client.ui.main.MainActivity
import dagger.Subcomponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@MainScope
@Subcomponent(
    modules = [
        MainModule::class,
        MainViewModelModule::class,
        MainFragmentsModule::class
    ])
interface MainComponent {

    @Subcomponent.Factory
    interface Factory{

        fun create(): MainComponent
    }

    fun inject(mainActivity: MainActivity)
}







