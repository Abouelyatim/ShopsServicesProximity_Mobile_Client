package com.smartcity.client.di

import android.app.Application
import com.smartcity.client.di.auth.AuthComponent
import com.smartcity.client.di.interest.InterestComponent
import com.smartcity.client.di.main.MainComponent
import com.smartcity.client.session.SessionManager
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        SubComponentsModule::class
    ]
)
interface AppComponent  {

    val sessionManager: SessionManager

    @Component.Builder
    interface Builder{

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(baseActivity: com.smartcity.client.ui.BaseActivity)

    fun inject(baseActivity: com.smartcity.client.ui.deleted.BaseActivity)

    fun authComponent(): AuthComponent.Factory

    fun mainComponent(): MainComponent.Factory

    fun interestComponent(): InterestComponent.Factory

}








