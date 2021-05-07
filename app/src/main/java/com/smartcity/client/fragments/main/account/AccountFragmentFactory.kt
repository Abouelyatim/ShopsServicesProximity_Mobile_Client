package com.smartcity.client.fragments.main.account

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.account.AccountFragment
import com.smartcity.client.ui.main.account.address.AddressFormFragment
import com.smartcity.client.ui.main.account.address.AddressFragment
import com.smartcity.client.ui.main.account.information.InformationFragment


import javax.inject.Inject

@MainScope
class AccountFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            AccountFragment::class.java.name -> {
                AccountFragment(
                    viewModelFactory,
                    requestManager)
            }

            AddressFragment::class.java.name -> {
                AddressFragment(
                    viewModelFactory,
                    requestManager)
            }

            AddressFormFragment::class.java.name -> {
                AddressFormFragment(
                    viewModelFactory,
                    requestManager)
            }

            InformationFragment::class.java.name -> {
                InformationFragment(
                    viewModelFactory,
                    requestManager)
            }

            else -> {
                AccountFragment(
                    viewModelFactory,
                    requestManager)
            }
        }


}