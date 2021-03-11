package com.smartcity.client.fragments.main.custom_category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager

import com.smartcity.client.di.main.MainScope

import com.smartcity.client.ui.main.custom_category.customCategory.CustomCategoryFragment

import javax.inject.Inject

@MainScope
class CustomCategoryFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment =

        when (className) {

            CustomCategoryFragment::class.java.name -> {
                CustomCategoryFragment(
                    viewModelFactory,
                    requestManager
                )
            }




            else -> {
                CustomCategoryFragment(
                    viewModelFactory,
                    requestManager
                )
            }
        }


}