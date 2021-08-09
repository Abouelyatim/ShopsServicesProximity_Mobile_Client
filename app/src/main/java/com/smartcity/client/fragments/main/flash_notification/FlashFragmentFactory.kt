package com.smartcity.client.fragments.main.flash_notification

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.flash_notification.address.ConfigSearchAddressFragment
import com.smartcity.client.ui.main.flash_notification.flash.FlashFlashNotificationFragment
import com.smartcity.client.ui.main.flash_notification.search.SearchFlashFragment
import com.smartcity.client.ui.main.flash_notification.search.ViewSearchFlashFragment
import com.smartcity.client.ui.main.flash_notification.viewproduct.ViewProductFlashFragment
import javax.inject.Inject

@MainScope
class FlashFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =

        when (className) {

            FlashFlashNotificationFragment::class.java.name -> {
                FlashFlashNotificationFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ViewProductFlashFragment::class.java.name -> {
                ViewProductFlashFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            SearchFlashFragment::class.java.name -> {
                SearchFlashFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ConfigSearchAddressFragment::class.java.name -> {
                ConfigSearchAddressFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ViewSearchFlashFragment::class.java.name -> {
                ViewSearchFlashFragment(
                    viewModelFactory,
                    requestManager
                )
            }

                else -> {
                FlashFlashNotificationFragment(
                    viewModelFactory,
                    requestManager
                )
            }
        }


}