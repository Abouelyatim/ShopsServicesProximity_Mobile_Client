package com.smartcity.client.fragments.main.cart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager

import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.cart.address.AddAddressFragment

import com.smartcity.client.ui.main.cart.cart.CartFragment
import com.smartcity.client.ui.main.cart.address.PickAddressFragment
import com.smartcity.client.ui.main.cart.information.AddUserInformationFragment
import com.smartcity.client.ui.main.cart.order.PlaceOrderFragment

import javax.inject.Inject

@MainScope
class CartFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =

        when (className) {

            CartFragment::class.java.name -> {
                CartFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            PlaceOrderFragment::class.java.name -> {
                PlaceOrderFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            PickAddressFragment::class.java.name -> {
                PickAddressFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            AddAddressFragment::class.java.name -> {
                AddAddressFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            AddUserInformationFragment::class.java.name -> {
                AddUserInformationFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            else -> {
                CartFragment(
                    viewModelFactory,
                    requestManager
                )
            }
        }


}