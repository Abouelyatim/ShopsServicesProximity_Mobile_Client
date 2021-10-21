package com.smartcity.client.ui.main.account

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.clearOrderList
import com.smartcity.client.ui.main.account.viewmodel.getOrderActionRecyclerPosition
import com.smartcity.client.ui.main.account.viewmodel.setOrderActionRecyclerPosition
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AccountFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_account,viewModelFactory){

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.displayBottomNavigation(true)

        subscribeObservers()

        addresses_settings.setOnClickListener {
            navAddress()
        }

        information_settings.setOnClickListener {
            navInformation()
        }

        orders_settings.setOnClickListener {
            navOrders()
        }

        around_stores_settings.setOnClickListener{
            navAroundStore()
        }
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

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
    }

    private fun navOrders(){
        viewModel.setOrderActionRecyclerPosition(0)
        viewModel.clearOrderList()
        findNavController().navigate(R.id.action_accountFragment_to_ordersFragment)
    }

    private fun navAddress(){
        findNavController().navigate(R.id.action_accountFragment_to_addressFragment)
    }

    private fun navInformation(){
        findNavController().navigate(R.id.action_accountFragment_to_informationFragment)
    }

    private fun navAroundStore(){
        findNavController().navigate(R.id.action_accountFragment_to_aroundStoresFragment)
    }
}