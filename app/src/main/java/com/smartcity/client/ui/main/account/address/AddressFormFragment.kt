package com.smartcity.client.ui.main.account.address

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
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.smartcity.client.R
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.*
import com.smartcity.client.util.GpsTracker
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import kotlinx.android.synthetic.main.fragment_set_delivery_address.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AddressFormFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_address_form,viewModelFactory),
    OnMapReadyCallback
{

    private lateinit var mMapView:MapView
    private var mGoogleMap: GoogleMap?=null

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
        uiCommunicationListener.displayBottomNavigation(false)

        initMap(savedInstanceState)
        saveEditTextValues()
        moveMarker()
        myPosition()
        saveDeliveryAddress()
        subscribeObservers()
        getDefaultCity()
    }

    private fun getDefaultCity() {
        viewModel.setStateEvent(
            AccountStateEvent.GetUserDefaultCityEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.CREATED_DONE)){
                    findNavController().popBackStack()
                }

                if(stateMessage.response.message.equals(AccountStateEvent.GetUserDefaultCityEvent().toString())){
                    setGoogleMapMarker()
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
    }

    private fun saveDeliveryAddress() {
        save_delivery_address_button.setOnClickListener {
            val address=viewModel.getNewAddress()
            address?.let {
                it.apartmentNumber=viewModel.getApartmentNumber()
                it.businessName=viewModel.getBusinessName()
                it.doorCodeName=viewModel.getDoorCodeName()
                viewModel.setNewAddress(it)
            }

            viewModel.getNewAddress()?.let {
                viewModel.setStateEvent(
                    AccountStateEvent.SaveAddressEvent(
                        it
                    )
                )
            }
        }
    }

    private fun prepareMapIntent(lat:Double,long:Double): Intent {
        val applicationInfo = requireActivity().packageManager.getApplicationInfo( activity?.packageName, PackageManager.GET_META_DATA)
        return PlacePicker.IntentBuilder()
            .setLatLong(lat, long)
            .showLatLong(false)
            .setMapZoom(16.0f)
            .setMapRawResourceStyle(R.raw.store_view_map_style)
            .setMapType(MapType.NORMAL)
            .setPlaceSearchBar(false, applicationInfo.metaData.getString("com.google.android.geo.API_KEY"))
            .setMarkerImageImageColor(R.color.bleu)
            .setFabColor(R.color.bleu)
            .setPrimaryTextColor(R.color.black)
            .setSecondaryTextColor(R.color.dark)
            .build(requireActivity())
    }

    private fun myPosition() {
        my_position_button.setOnClickListener {
            val gpsTracker =
                GpsTracker(requireContext())
            if(uiCommunicationListener.isFineLocationPermissionGranted()){
                gpsTracker.getCurrentLocation()
                val latitude: Double = gpsTracker.getLatitude()
                val longitude: Double = gpsTracker.getLongitude()

                val intent = prepareMapIntent(latitude,longitude)
                startForResult.launch(intent)
            }else{
                uiCommunicationListener.isFineLocationPermissionGranted()
            }
        }
    }

    private fun moveMarker() {
        move_marker_button.setOnClickListener {
            viewModel.getDefaultCity()?.let {
                val latitude: Double = it.lat
                val longitude: Double = it.lon

                val intent =  prepareMapIntent(latitude,longitude)
                startForResult.launch(intent)
            }
        }
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val addressData = result.data?.getParcelableExtra<AddressData>(com.sucho.placepicker.Constants.ADDRESS_INTENT)

            var address: Address?=null
            addressData?.let {
                it.addressList?.let {
                    address=it.first()
                }
            }

            address?.let {

                viewModel.setNewAddress(
                    com.smartcity.client.models.Address(
                        -1,
                        it.featureName,
                        it.adminArea,
                        it.subAdminArea,
                        it.locality,
                        it.thoroughfare,
                        it.postalCode,
                        it.countryCode,
                        it.countryName,
                        addressData!!.latitude,
                        addressData!!.longitude,
                        it.getAddressLine(0),
                        "",
                        "",
                        "",
                        -1
                    )
                )
            }
        }
    }

    private fun saveEditTextValues() {
        apartment_number.addTextChangedListener {
            viewModel.setApartmentNumber(it.toString())
        }

        business_name.addTextChangedListener {
            viewModel.setBusinessName(it.toString())
        }

        door_code_name.addTextChangedListener {
            viewModel.setDoorCodeName(it.toString())
        }
    }

    private fun setAddress() {
        viewModel.getNewAddress()?.let {
            address_text.text=it.fullAddress
        }
        apartment_number.setText(viewModel.getApartmentNumber())
        business_name.setText(viewModel.getBusinessName())
        door_code_name.setText(viewModel.getDoorCodeName())
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initMap(savedInstanceState: Bundle?) {
        mMapView = requireActivity().findViewById(R.id.delivery_map_address) as MapView
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume() // needed to get the map to display immediately
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap!!.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(context,R.raw.store_view_map_style)
        )
        mGoogleMap=googleMap
        setGoogleMapMarker()
    }

    private fun setGoogleMapMarker(){
        viewModel.getDefaultCity()?.let { city ->
            mGoogleMap?.let {
                val zoomLevel = 16.0f
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(city.lat, city.lon), zoomLevel))
                it.uiSettings.setAllGesturesEnabled(false)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setAddress()
    }
}