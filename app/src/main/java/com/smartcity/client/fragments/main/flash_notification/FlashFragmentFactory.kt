package com.smartcity.client.fragments.main.flash_notification

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.flash_notification.FlashFlashNotificationFragment
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


            else -> {
                FlashFlashNotificationFragment(
                    viewModelFactory,
                    requestManager
                )
            }
        }


}