package com.smartcity.client.fragments.main.blog

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.blog.BlogFragment
import com.smartcity.client.ui.main.blog.store.StoreFragment
import com.smartcity.client.ui.main.blog.viewProduct.ViewProductFragment

import javax.inject.Inject

@MainScope
class BlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            BlogFragment::class.java.name -> {
                BlogFragment(viewModelFactory, requestManager)
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

            else -> {
                BlogFragment(viewModelFactory, requestManager)
            }
        }


}