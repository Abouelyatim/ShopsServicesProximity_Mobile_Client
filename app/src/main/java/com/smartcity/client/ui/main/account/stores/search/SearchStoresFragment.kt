package com.smartcity.client.ui.main.account.stores.search

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.map.locationpicker.CircleData
import com.map.locationpicker.Constants
import com.map.locationpicker.LocationPicker
import com.smartcity.client.R
import com.smartcity.client.models.City
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.*
import com.smartcity.client.util.StateMessageCallback
import kotlinx.android.synthetic.main.fragment_search_stores.*
import kotlinx.android.synthetic.main.fragment_search_stores.back_button
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class SearchStoresFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_search_stores,viewModelFactory){

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.cancelActiveJobs()
        super.onCreate(savedInstanceState)
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
        uiCommunicationListener.hideSoftKeyboard()
        uiCommunicationListener.displayBottomNavigation(false)

        subscribeObservers()
        navConfigAddress()
        setSearchAddress()
        setCategory()
        backProceed()
        setDistance()
        navViewSearch()
        navCategory()
    }

    private fun setCategory() {
        stores_category.text = viewModel.getSelectedCategory()
        if(viewModel.getSelectedCategory().isEmpty()){
            stores_category.text = "No category selected"
        }
    }

    private fun navCategory() {
        stores_category_container.setOnClickListener {
            findNavController().navigate(R.id.action_searchStoresFragment_to_searchStoresSelectCategoryFragment)
        }
    }

    private fun navViewSearch() {
        search_stores_button.setOnClickListener {
            findNavController().navigate(R.id.action_searchStoresFragment_to_viewSearchStoresFragment)
        }
    }

    private fun initIntentLocationPicker(lat: Double,lon: Double): Intent {
        val applicationInfo = requireActivity().packageManager.getApplicationInfo( activity?.packageName, PackageManager.GET_META_DATA)
        return LocationPicker.IntentBuilder()
            .setLatLong(lat, lon)
            .setMapRawResourceStyle(R.raw.store_view_map_style)
            .setMapType(com.map.locationpicker.MapType.NORMAL)
            .setPlaceSearchBar(applicationInfo.metaData.getString("com.google.android.geo.API_KEY"))
            .setMarkerImageImageColor(R.color.bleu)
            .setFabColor(R.color.bleu)
            .setPrimaryTextColor(R.color.black)
            .setCircleBackgroundColorRes(R.color.blue_light2)
            .setSliderThumbTintColor(R.color.bleu)
            .setSliderTrackActiveTintColor(R.color.bleu)
            .setSliderTrackInactiveTintColor(R.color.dark)
            .setConfirmButtonBackgroundShape(R.drawable.radius_button_blue)
            .setConfirmButtonTextColor(R.color.white)
            .setBottomViewColor(R.color.white)
            .setConfirmButtonText("Confirm")
            .setInitialCircleRadiusKilometer(viewModel.getSearchRadius())
            .setSliderValueFrom(1.0F)
            .setSliderValueTo(200.0F)
            .hideLocationButton(false)
            .enableMapGesturesEnabled(true)
            .build(requireActivity())
    }

    @SuppressLint("SetTextI18n")
    private fun setDistance() {
        stores_configure_distance.setOnClickListener {
            val intent = initIntentLocationPicker( viewModel.getSearchCity()!!.lat, viewModel.getSearchCity()!!.lon)
            startForResult.launch(intent)
        }
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val circleData = result.data?.getParcelableExtra<CircleData>(
                Constants.CIRCLE_INTENT)
            var address: Address?=null
            var radius: Double?=null
            circleData?.let {
                it.addressList?.let {
                    address=it.first()
                }

                radius = it.radius
            }

            address?.let {
                val city= City(
                    it.latitude,
                    it.longitude,
                    it.getAddressLine(0),
                    -1,
                    "",
                    ""
                )

                viewModel.setSearchRadius(radius!!)
                viewModel.setSearchCity(city)
            }
        }
    }

    private fun navConfigAddress() {
        stores_configure_search_address.setOnClickListener {
            findNavController().navigate(R.id.action_searchStoresFragment_to_searchStoresConfigAddressFragment)
        }
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setSearchAddress() {
        if(viewModel.getSearchCity()==null){
            viewModel.setSearchCity( viewModel.getDefaultCity()!!)
        }
        stores_search_address.text=viewModel.getSearchCity()!!.name
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

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if(viewModel.checkSearchRadius()){
                stores_search_radius.text = BigDecimal(viewModel.getSearchRadius()).setScale(0, RoundingMode.UP).toString()
                stores_configure_distance.visibility = View.VISIBLE
            }else{
                stores_configure_distance.visibility = View.GONE
            }
        })
    }

}