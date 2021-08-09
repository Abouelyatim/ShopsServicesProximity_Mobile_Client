package com.smartcity.client.ui.main.flash_notification.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.flash_notification.BaseFlashNotificationFragment
import com.smartcity.client.ui.main.flash_notification.state.CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.ui.main.flash_notification.viewmodel.*
import kotlinx.android.synthetic.main.fragment_search_flash.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class SearchFlashFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseFlashNotificationFragment(R.layout.fragment_search_flash,viewModelFactory)
{
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY] as FlashViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.displayBottomNavigation(false)

        setSearchAddress()
        navConfigAddress()
        navSearch()
    }

    private fun navSearch() {
        search_flash_button.setOnClickListener {
            viewModel.setSearchOfferActionRecyclerPosition(0)
            viewModel.clearSearchFlashDealsMap()
            viewModel.clearSearchProductDiscountList()
            findNavController().navigate(R.id.action_searchFlashFragment_to_viewSearchFlashFragment)
        }
    }

    private fun navConfigAddress() {
        configure_search_address.setOnClickListener {
            findNavController().navigate(R.id.action_searchFlashFragment_to_configSearchAddressFragment)
        }
    }

    private fun setSearchAddress() {
        if(viewModel.getSearchCity()==null){
            viewModel.setSearchCity( viewModel.getDefaultCity()!!)
        }
        search_address.text=viewModel.getSearchCity()!!.name
    }
}