package com.smartcity.client.ui.interest

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.hbb20.CountryCodePicker
import com.smartcity.client.R
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.ui.interest.state.InterestStateEvent
import com.smartcity.client.ui.interest.viewmodel.InterestViewModel
import com.smartcity.client.ui.interest.viewmodel.setCountry
import com.smartcity.client.util.RetryToHandelNetworkError
import com.smartcity.client.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_select_country.*
import javax.inject.Inject

@InterestScope
class SelectCountryFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseInterestFragment(R.layout.fragment_select_country),
    RetryToHandelNetworkError
{

    val viewModel: InterestViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
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
            InterestStateEvent.UserInterestCenter()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)

            dataState.data?.let { data ->
                data.response?.let{event ->
                    //check if user set interest center if not send request to get category list
                    event.peekContent().let{ response ->
                        response.message?.let{ message ->
                            if(message.equals(SuccessHandling.DONE_User_Interest_Center)){
                                data.data?.let {
                                    if(it.peekContent().categoryFields.categoryList.isEmpty()){

                                    }
                                }
                            }
                        }
                    }
                }

            }
        })
    }

    fun navSelectCity(){
        findNavController().navigate(R.id.action_selectCountryFragment_to_configureAddressFragment)
    }

    override fun resendNetworkRequest() {

    }

    override fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }
}