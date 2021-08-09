package com.smartcity.client.ui.main.flash_notification.address

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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.City
import com.smartcity.client.ui.interest.city.CityAdapter
import com.smartcity.client.ui.interest.viewmodel.setCity
import com.smartcity.client.ui.interest.viewmodel.setCityList
import com.smartcity.client.ui.main.flash_notification.BaseFlashNotificationFragment
import com.smartcity.client.ui.main.flash_notification.state.CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.flash_notification.state.FlashStateEvent
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.ui.main.flash_notification.viewmodel.*
import com.smartcity.client.util.GpsTracker
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.TopSpacingItemDecoration
import com.sucho.placepicker.AddressData
import com.sucho.placepicker.MapType
import com.sucho.placepicker.PlacePicker
import kotlinx.android.synthetic.main.fragment_config_search_address.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ConfigSearchAddressFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseFlashNotificationFragment(R.layout.fragment_config_search_address,viewModelFactory),
    CityAdapter.Interaction
{
    private lateinit var cityRecyclerAdapter: CityAdapter

    private lateinit var citySearchView: SearchView

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

        subscribeObservers()
        initSearchView()
        initCityRecyclerView()
        selectMyPosition()
        defaultPosition()
        backProceed()
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun defaultPosition() {
        flash_default_position_button.setOnClickListener {
            viewModel.setCityQuery("")
            viewModel.setCityList(
                listOf(
                    viewModel.getDefaultCity()!!
                )
            )
        }
    }

    private fun selectMyPosition() {
        val applicationInfo = requireActivity().packageManager.getApplicationInfo( activity?.packageName, PackageManager.GET_META_DATA)
        flash_my_position_button.setOnClickListener {
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
                    "",
                    ""
                )

                viewModel.setCityQuery("")
                viewModel.setCityList(
                    listOf(city)
                )
            }
        }
    }

    private fun initCityRecyclerView() {
        flash_address_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ConfigSearchAddressFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            cityRecyclerAdapter =
                CityAdapter(
                    this@ConfigSearchAddressFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                }
            })
            adapter = cityRecyclerAdapter
        }
    }

    private fun initSearchView() {
        activity?.apply {
            val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            citySearchView = flash_city_search_view
            citySearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            citySearchView.maxWidth = Integer.MAX_VALUE
            citySearchView.setIconifiedByDefault(false)
            citySearchView.isSubmitButtonEnabled = true
            citySearchView.queryHint="Enter your city"

        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = citySearchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setText(viewModel.getCityQuery())

        val backgroundView = citySearchView.findViewById(R.id.search_plate) as View
        backgroundView.background = null

        searchPlate.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: ${searchQuery}")
                viewModel.setCityQuery(
                    searchQuery
                )
                resolveAddress()
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = citySearchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: ${searchQuery}")
            viewModel.setCityQuery(
                searchQuery
            )
            resolveAddress()
        }
    }

    private fun resolveAddress(){
        viewModel.setStateEvent(
            FlashStateEvent.ResolveUserAddressEvent()
        )
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
            cityRecyclerAdapter.submitList(viewModel.getCityList())
        })
    }

    override fun onItemSelected(city: City) {
        viewModel.setSearchCity(city)
        findNavController().popBackStack()
    }

}