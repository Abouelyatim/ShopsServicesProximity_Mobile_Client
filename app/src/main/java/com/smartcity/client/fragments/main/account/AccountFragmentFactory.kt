package com.smartcity.client.fragments.main.account

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.account.AccountFragment
import com.smartcity.client.ui.main.account.address.AddressFormFragment
import com.smartcity.client.ui.main.account.address.AddressFragment
import com.smartcity.client.ui.main.account.information.InformationFragment
import com.smartcity.client.ui.main.account.orders.OrdersFragment
import com.smartcity.client.ui.main.account.orders.ViewOrderFragment
import com.smartcity.client.ui.main.account.stores.AroundStoresFragment
import com.smartcity.client.ui.main.account.stores.search.SearchStoresConfigAddressFragment
import com.smartcity.client.ui.main.account.stores.search.SearchStoresFragment
import com.smartcity.client.ui.main.account.stores.search.SearchStoresSelectCategoryFragment
import com.smartcity.client.ui.main.account.stores.search.ViewSearchStoresFragment


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

            OrdersFragment::class.java.name -> {
                OrdersFragment(
                    viewModelFactory,
                    requestManager)
            }

            ViewOrderFragment::class.java.name -> {
                ViewOrderFragment(
                    viewModelFactory,
                    requestManager)
            }

            AroundStoresFragment::class.java.name -> {
                AroundStoresFragment(
                    viewModelFactory,
                    requestManager)
            }

            SearchStoresFragment::class.java.name -> {
                SearchStoresFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            SearchStoresConfigAddressFragment::class.java.name -> {
                SearchStoresConfigAddressFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ViewSearchStoresFragment::class.java.name -> {
                ViewSearchStoresFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            SearchStoresSelectCategoryFragment::class.java.name -> {
                SearchStoresSelectCategoryFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            else -> {
                AccountFragment(
                    viewModelFactory,
                    requestManager)
            }
        }


}