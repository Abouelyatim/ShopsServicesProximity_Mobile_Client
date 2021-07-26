package com.smartcity.client.ui.interest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.smartcity.client.R
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.ui.interest.viewmodel.InterestViewModel
import com.smartcity.client.ui.interest.viewmodel.getHomeAddress
import com.smartcity.client.ui.interest.viewmodel.getSavedHomeAddress
import com.smartcity.client.util.RetryToHandelNetworkError
import kotlinx.android.synthetic.main.fragment_configure_delivery_address.*
import javax.inject.Inject

@InterestScope
class ConfigureDeliveryAddressFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseInterestFragment(R.layout.fragment_configure_delivery_address),
    RetryToHandelNetworkError
{

    val viewModel: InterestViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
    }

    override fun resendNetworkRequest() {

    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
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