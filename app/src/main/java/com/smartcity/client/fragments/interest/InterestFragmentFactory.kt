package com.smartcity.client.fragments.interest

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.ui.interest.ChooseInterestFragment
import javax.inject.Inject


@InterestScope
class InterestFragmentFactory
@Inject
constructor(

    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            ChooseInterestFragment::class.java.name -> {
                ChooseInterestFragment(viewModelFactory)
            }



            else -> {
                ChooseInterestFragment(viewModelFactory)
            }
        }


}