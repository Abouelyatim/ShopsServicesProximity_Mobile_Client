package com.smartcity.client.fragments.main.custom_category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager

import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.custom_category.createOption.CreateOptionFragment
import com.smartcity.client.ui.main.custom_category.createProduct.CreateProductFragment
import com.smartcity.client.ui.main.custom_category.customCategory.CustomCategoryFragment
import com.smartcity.client.ui.main.custom_category.option.OptionFragment
import com.smartcity.client.ui.main.custom_category.optionValue.OptionValuesFragment
import com.smartcity.client.ui.main.custom_category.product.ProductFragment
import com.smartcity.client.ui.main.custom_category.renameOption.RenameOptionFragment

import com.smartcity.client.ui.main.custom_category.variant.VariantFragment
import com.smartcity.client.ui.main.custom_category.viewProduct.ViewProductFragment
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

            ProductFragment::class.java.name -> {
                ProductFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            CreateProductFragment::class.java.name -> {
                CreateProductFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            OptionFragment::class.java.name -> {
                OptionFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            OptionValuesFragment::class.java.name -> {
                OptionValuesFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            CreateOptionFragment::class.java.name -> {
                CreateOptionFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            VariantFragment::class.java.name -> {
                VariantFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            RenameOptionFragment::class.java.name -> {
                RenameOptionFragment(
                    viewModelFactory,
                    requestManager
                )
            }

            ViewProductFragment::class.java.name -> {
                ViewProductFragment(
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