package com.smartcity.client.ui.interest

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hbb20.CountryCodePicker
import com.smartcity.client.R
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.ui.interest.state.InterestStateEvent
import com.smartcity.client.ui.interest.viewmodel.setCountry
import com.smartcity.client.util.RetryToHandelNetworkError
import kotlinx.android.synthetic.main.fragment_select_country.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@InterestScope
class SelectCountryFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseInterestFragment(R.layout.fragment_select_country,viewModelFactory),
    RetryToHandelNetworkError
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as InterestActivity).initHandelNetworkError(this)
        getUserInterestCenter()
        subscribeObservers()

        select_country_next_button.setOnClickListener {
            navSelectCity()
        }

        viewModel.setCountry(ccp.selectedCountryName)
        ccp.setOnCountryChangeListener(object : CountryCodePicker.OnCountryChangeListener{
            override fun onCountrySelected() {
                viewModel.setCountry(ccp.selectedCountryName)
            }
        })
    }

    private fun getUserInterestCenter() {
        viewModel.setStateEvent(
            InterestStateEvent.UserInterestCenterEvent()
        )
    }

    private fun subscribeObservers() {

    }

    fun navSelectCity(){
        findNavController().navigate(R.id.action_selectCountryFragment_to_configureAddressFragment)
    }

    override fun resendNetworkRequest() {

    }
}