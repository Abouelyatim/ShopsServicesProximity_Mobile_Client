package com.smartcity.client.di.auth

import com.smartcity.client.ui.auth.*
import dagger.Subcomponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@AuthScope
@Subcomponent(
    modules = [
        AuthModule::class,
        AuthViewModelModule::class,
        AuthFragmentsModule::class
    ])
interface AuthComponent {

    @Subcomponent.Factory
    interface Factory{

        fun create(): AuthComponent
    }

    fun inject(authActivity: AuthActivity)
}
