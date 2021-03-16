package com.smartcity.client.di

import com.smartcity.client.di.auth.AuthComponent
import com.smartcity.client.di.interest.InterestComponent
import com.smartcity.client.di.main.MainComponent
import dagger.Module

@Module(
    subcomponents = [
        AuthComponent::class,
        MainComponent::class,
        InterestComponent::class
    ]
)
class SubComponentsModule