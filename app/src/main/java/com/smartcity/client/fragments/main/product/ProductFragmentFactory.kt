package com.smartcity.client.fragments.main.product

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.product.foryou.ForYouFragment
import com.smartcity.client.ui.main.product.products.ProductFragment
import com.smartcity.client.ui.main.product.search.SearchProductFragment
import com.smartcity.client.ui.main.product.store.StoreFragment
import com.smartcity.client.ui.main.product.viewProduct.ViewProductFragment

import javax.inject.Inject

@MainScope
class ProductFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            ProductFragment::class.java.name -> {
                ProductFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ViewProductFragment::class.java.name -> {
                ViewProductFragment(viewModelFactory, requestManager)
            }

            StoreFragment::class.java.name -> {
                StoreFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            SearchProductFragment::class.java.name -> {
                SearchProductFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ForYouFragment::class.java.name -> {
                ForYouFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            else -> {
                ProductFragment(
                    viewModelFactory,
                    requestManager
                )
            }
        }


}