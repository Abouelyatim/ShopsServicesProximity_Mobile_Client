package com.smartcity.client.fragments.main.store

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.store.StoreFragment


import javax.inject.Inject

@MainScope
class StoreFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            StoreFragment::class.java.name -> {
                StoreFragment(
                    viewModelFactory,
                    requestManager)
            }



            else -> {
                StoreFragment(
                    viewModelFactory,
                    requestManager)
            }
        }


}