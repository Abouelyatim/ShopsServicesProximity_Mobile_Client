package com.smartcity.client.di

import com.smartcity.client.di.auth.AuthComponent
import com.smartcity.client.di.main.MainComponent
import dagger.Module

@Module(
    subcomponents = [
        AuthComponent::class,
        MainComponent::class
    ]
)
class SubComponentsModule