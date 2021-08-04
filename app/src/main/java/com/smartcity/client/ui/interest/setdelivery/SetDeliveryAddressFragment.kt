package com.smartcity.client.ui.interest.setdelivery

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.smartcity.client.R
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.ui.interest.BaseInterestFragment
import com.smartcity.client.util.GpsTracker
import com.smartcity.client.ui.interest.InterestActivity
import com.smartcity.client.ui.interest.state.InterestStateEvent
import com.smartcity.client.ui.interest.viewmodel.*
import com.smartcity.client.util.RetryToHandelNetworkError
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
@InterestScope
class SetDeliveryAddressFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseInterestFragment(R.layout.fragment_set_delivery_address,viewModelFactory),
    RetryToHandelNetworkError,
    OnMapReadyCallback
{
    private lateinit var mMapView:MapView
    private var mGoogleMap:GoogleMap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun resendNetworkRequest() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as InterestActivity).initHandelNetworkError(this)

        initMap(savedInstanceState)
        saveEditTextValues()
        moveMarker()
        myPosition()
        saveDeliveryAddress()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must
            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.DONE_Create_Address)){
                    findNavController().popBackStack()
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
            viewModel.setSavedHomeAddress(
                viewModel.getHomeAddress()
            )

            val address=viewModel.getNetworkHomeAddress()
            address?.let {
                it.apartmentNumber=viewModel.getApartmentNumber()
                it.businessName=viewModel.getBusinessName()
                it.doorCodeName=viewModel.getDoorCodeName()
                viewModel.setNetworkHomeAddress(it)
            }

            viewModel.getNetworkHomeAddress()?.let {
                viewModel.setStateEvent(
                    InterestStateEvent.CreateAddressEvent(
                        it
                    )
                )
            }
        }
    }

    private fun prepareMapIntent(lat:Double,long:Double):Intent{
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
            val latitude: Double = viewModel.getHomeLat()
            val longitude: Double = viewModel.getHomeLong()

            val intent =  prepareMapIntent(latitude,longitude)
            startForResult.launch(intent)
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
                viewModel.setHomeLat(
                   it.latitude
               )

                viewModel.setHomeLong(
                    it.longitude
                )

                viewModel.setHomeAddress(
                    it.getAddressLine(0)
                )

                viewModel.setNetworkHomeAddress(
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
        address_text.text=viewModel.getHomeAddress()
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
        mGoogleMap?.let {
            val zoomLevel = 16.0f
            it.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(viewModel.getHomeLat(), viewModel.getHomeLong()), zoomLevel))
            it.uiSettings.setAllGesturesEnabled(false)
        }
    }

    override fun onResume() {
        super.onResume()
        setGoogleMapMarker()
        if(viewModel.getHomeLat()==0.0 || viewModel.getHomeLong()==0.0){
            viewModel.setHomeLat(
                viewModel.getSelectedCity()!!.lat
            )
            viewModel.setHomeLong(
                viewModel.getSelectedCity()!!.lon
            )
        }

        if (viewModel.getHomeAddress().isEmpty()){
            viewModel.setHomeAddress(
                viewModel.getSelectedCity()!!.name
            )
        }
        setAddress()
    }
}