package com.smartcity.client

import android.app.Application
import com.smartcity.client.di.AppComponent
import com.smartcity.client.di.DaggerAppComponent
import com.smartcity.client.di.auth.AuthComponent
import com.smartcity.client.di.interest.InterestComponent
import com.smartcity.client.di.main.MainComponent

class BaseApplication : Application(){

    lateinit var appComponent: AppComponent

    private var authComponent: AuthComponent? = null

    private var interestComponent: InterestComponent? = null

    private var mainComponent: MainComponent? = null

    override fun onCreate() {
        super.onCreate()
        initAppComponent()
    }

    fun releaseMainComponent(){
        mainComponent = null
    }

    fun mainComponent(): MainComponent {
        if(mainComponent == null){
            mainComponent = appComponent.mainComponent().create()
        }
        return mainComponent as MainComponent
    }

    fun releaseAuthComponent(){
        authComponent = null
    }

    fun authComponent(): AuthComponent {
        if(authComponent == null){
            authComponent = appComponent.authComponent().create()
        }
        return authComponent as AuthComponent
    }

    fun releaseInterestComponent(){
        interestComponent = null
    }

    fun interestComponent(): InterestComponent {
        if(interestComponent == null){
            interestComponent = appComponent.interestComponent().create()
        }
        return interestComponent as InterestComponent
    }

    fun initAppComponent(){
        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
    }


}