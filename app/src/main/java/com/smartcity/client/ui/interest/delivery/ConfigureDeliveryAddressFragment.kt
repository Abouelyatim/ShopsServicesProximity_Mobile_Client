package com.smartcity.client.ui.interest.delivery

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smartcity.client.R
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.ui.interest.BaseInterestFragment
import com.smartcity.client.ui.interest.InterestActivity
import com.smartcity.client.ui.interest.viewmodel.getSavedHomeAddress
import com.smartcity.client.util.RetryToHandelNetworkError
import kotlinx.android.synthetic.main.fragment_configure_delivery_address.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@InterestScope
class ConfigureDeliveryAddressFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseInterestFragment(R.layout.fragment_configure_delivery_address,viewModelFactory),
    RetryToHandelNetworkError
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun resendNetworkRequest() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as InterestActivity).initHandelNetworkError(this)

        setDeliveryAddressText()
        skip()
        next()
        home_address.setOnClickListener {
            navSetAddress()
        }
    }

    private fun next() {
        delivery_address_next_button.setOnClickListener {
            navInterest()
        }
    }

    private fun skip() {
        skip_button.setOnClickListener {
            navInterest()
        }
    }

    private fun navInterest(){
        findNavController().navigate(R.id.action_configureDeliveryAddressFragment_to_chooseInterestFragment)
    }

    private fun setDeliveryAddressText() {
        if (viewModel.getSavedHomeAddress().isNotEmpty()){
            delivery_address.text=viewModel.getSavedHomeAddress()
        }else{
            delivery_address.text="Add address"
        }
    }

    private fun navSetAddress(){
        findNavController().navigate(R.id.action_configureDeliveryAddressFragment_to_setDeliveryAddressFragment)
    }
}