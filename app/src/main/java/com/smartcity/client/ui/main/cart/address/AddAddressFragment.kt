package com.smartcity.client.ui.main.cart.address

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.Address
import com.smartcity.client.ui.main.cart.BaseCartFragment
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.cart.state.CartStateEvent
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.ui.main.cart.viewmodel.CartViewModel
import com.smartcity.client.ui.main.cart.viewmodel.setAddressList
import com.smartcity.client.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_add_address.*
import javax.inject.Inject


class AddAddressFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCartFragment(R.layout.fragment_add_address)
{
    val viewModel: CartViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as CartViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    /**
     * !IMPORTANT!
     * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
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
                            it.orderFields.addressList?.let {
                                viewModel.setAddressList(it)
                            }
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        })
    }

    private fun saveAddress() {
       /* save_address_button.setOnClickListener {
            viewModel.setStateEvent(
                CartStateEvent.SaveAddress(
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
            CartStateEvent.GetUserAddresses()
        )
    }
}