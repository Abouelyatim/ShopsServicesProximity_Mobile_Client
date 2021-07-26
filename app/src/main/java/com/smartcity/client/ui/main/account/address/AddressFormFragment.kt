package com.smartcity.client.ui.main.account.address

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.Address
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.AccountViewModel
import com.smartcity.client.ui.main.account.viewmodel.setAddressList
import com.smartcity.client.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_address_form.*
import javax.inject.Inject


class AddressFormFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_address_form){

    val viewModel: AccountViewModel by viewModels{
        viewModelFactory
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        stateChangeListener.expandAppBar()
        stateChangeListener.displayBottomNavigation(false)

        saveAddress()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, androidx.lifecycle.Observer {dataState->
            stateChangeListener.onDataStateChange(dataState)
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(!data.response.hasBeenHandled){
                            if (response.message== SuccessHandling.CREATED_DONE){
                                updateAddressList()
                            }
                        }
                    }
                }

                //handle request response to update address list
                dataState.data?.let { data ->
                    data.data?.let{
                        it.getContentIfNotHandled()?.let{
                            viewModel.setAddressList(it.addressList)
                            navAddress()
                        }
                    }
                }
            }
        })
    }

    private fun navAddress() {
        findNavController().popBackStack()
    }

    private fun saveAddress() {
       /* save_address_button.setOnClickListener {
            viewModel.setStateEvent(
                AccountStateEvent.SaveAddress(
                    Address(
                        null,
                        if(input_house_number.text.toString().isEmpty()) null else input_house_number.text.toString().toInt(),
                        input_street.text.toString(),
                        input_city.text.toString(),
                        if(input_zip_code.text.toString().isEmpty()) null else input_zip_code.text.toString().toLong(),
                        -1
                    )
                )
            )
        }*/
    }

    private fun updateAddressList(){
        viewModel.setStateEvent(
            AccountStateEvent.GetUserAddresses()
        )
    }
}