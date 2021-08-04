package com.smartcity.client.ui.main.cart.address

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.ui.main.cart.BaseCartFragment
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.cart.state.CartStateEvent
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.ui.main.cart.viewmodel.getAddressList
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AddAddressFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCartFragment(R.layout.fragment_add_address,viewModelFactory)
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.displayBottomNavigation(false)

        saveAddress()
        subscribeObservers()
    }
    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.CREATED_DONE)){
                    updateAddressList()
                }

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->//must
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if (viewModel.getAddressList().isNotEmpty()){
                findNavController().popBackStack()
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
            CartStateEvent.GetUserAddressesEvent()
        )
    }
}