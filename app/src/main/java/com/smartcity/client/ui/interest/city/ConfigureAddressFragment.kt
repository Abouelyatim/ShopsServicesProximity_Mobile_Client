package com.smartcity.client.ui.interest.city

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartcity.client.R
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.models.City
import com.smartcity.client.ui.interest.BaseInterestFragment
import com.smartcity.client.util.GpsTracker
import com.smartcity.client.ui.interest.InterestActivity
import com.smartcity.client.ui.interest.state.InterestStateEvent
import com.smartcity.client.ui.interest.viewmodel.*
import com.smartcity.client.util.RetryToHandelNetworkError
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import kotlinx.android.synthetic.main.fragment_configure_address.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
@InterestScope
class ConfigureAddressFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
): BaseInterestFragment(R.layout.fragment_configure_address,viewModelFactory),
    RetryToHandelNetworkError,
    CityAdapter.Interaction
{
    private lateinit var cityRecyclerAdapter: CityAdapter

    private lateinit var citySearchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as InterestActivity).initHandelNetworkError(this)
        subscribeObservers()
        initSearchView()
        initCityRecyclerView()
        selectMyPosition()
    }

    private fun selectMyPosition() {
        val applicationInfo = requireActivity().packageManager.getApplicationInfo( activity?.packageName, PackageManager.GET_META_DATA)
        my_position_button.setOnClickListener {
            val gpsTracker =
                GpsTracker(requireContext())
            if(uiCommunicationListener.isFineLocationPermissionGranted()){
                gpsTracker.getCurrentLocation()
                val latitude: Double = gpsTracker.getLatitude()
                val longitude: Double = gpsTracker.getLongitude()

                val intent = PlacePicker.IntentBuilder()
                    .setLatLong(latitude, longitude)
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

                startForResult.launch(intent)
            }else{
                uiCommunicationListener.isFineLocationPermissionGranted()
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

                val city=City(
                    it.latitude,
                    it.longitude,
                    it.getAddressLine(0),
                    -1,
                    ""
                )

                viewModel.setCity("")
                viewModel.setCityList(
                    listOf(city)
                )
            }
        }
    }

    private fun initSearchView(){
        activity?.apply {
            val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            citySearchView = city_search_view
            citySearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            citySearchView.maxWidth = Integer.MAX_VALUE
            citySearchView.setIconifiedByDefault(false)
            citySearchView.isSubmitButtonEnabled = true
            citySearchView.queryHint="Enter your city"

        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = citySearchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setText(viewModel.getCity())

        val backgroundView = citySearchView.findViewById(R.id.search_plate) as View
        backgroundView.background = null

        searchPlate.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: ${searchQuery}")
                viewModel.setCity(
                    searchQuery
                )
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = citySearchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: ${searchQuery}")
            viewModel.setCity(
                searchQuery
            )
            resolveAddress()
        }
    }

    private fun resolveAddress(){
        viewModel.setStateEvent(
            InterestStateEvent.ResolveUserAddressEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must
            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.DONE_User_Default_City)){
                    navDeliveryAddress()
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
            cityRecyclerAdapter.submitList(viewModel.getCityList())
        })
    }

    override fun resendNetworkRequest() {

    }

    private fun initCityRecyclerView(){
        address_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ConfigureAddressFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            cityRecyclerAdapter =
                CityAdapter(
                    this@ConfigureAddressFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                }
            })
            adapter = cityRecyclerAdapter
        }
    }

    override fun onItemSelected(city: City) {
        viewModel.setSelectedCity(
            city
        )
        viewModel.setStateEvent(
            InterestStateEvent.SetUserDefaultCityEvent(
                city
            )
        )
    }

    private fun navDeliveryAddress(){
        findNavController().navigate(R.id.action_configureAddressFragment_to_configureDeliveryAddressFragment)
    }
}