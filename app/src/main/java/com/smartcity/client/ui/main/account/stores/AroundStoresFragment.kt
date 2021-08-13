package com.smartcity.client.ui.main.account.stores

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.map.locationpicker.CircleData
import com.map.locationpicker.Constants
import com.map.locationpicker.LocationPicker
import com.map.locationpicker.MapType
import com.smartcity.client.R
import com.smartcity.client.models.Store
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.*
import com.smartcity.client.ui.main.flash_notification.flash.StoreBottomSheetDialog
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_around_stores.*
import kotlinx.android.synthetic.main.fragment_around_stores.back_button
import kotlinx.android.synthetic.main.fragment_around_stores.focusable_view
import kotlinx.android.synthetic.main.fragment_for_you.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class AroundStoresFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_around_stores,viewModelFactory),
    StoresAdapter.Interaction
{

    private lateinit var recyclerStoresAdapter: StoresAdapter

    private lateinit var  applicationInfo : ApplicationInfo

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

        applicationInfo = requireActivity().packageManager.getApplicationInfo(requireActivity().packageName, PackageManager.GET_META_DATA)

        subscribeObservers()
        initRecyclerView()

        changeRadius()
        getDefaultCity()
        backProceed()
        navSearch()
    }

    private fun navSearch() {
        stores_search.setOnClickListener {
            findNavController().navigate(R.id.action_aroundStoresFragment_to_searchStoresFragment)
        }
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun changeRadius() {
        location_container.setOnClickListener {
            getLocation()
        }
    }

    private fun getDefaultCity(){
        viewModel.setStateEvent(
            AccountStateEvent.GetUserDefaultCityEvent()
        )
    }

    fun initRecyclerView(){
        stores_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@AroundStoresFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerStoresAdapter =
                StoresAdapter(
                    this@AroundStoresFragment,
                    requestManager
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = recyclerStoresAdapter
        }

    }

    private fun getStores(){
        viewModel.setStateEvent(
            AccountStateEvent.GetStoresAroundEvent(
                viewModel.getDefaultCity()!!.lat,
                viewModel.getDefaultCity()!!.lon,
                viewModel.getRadius()
            )
        )
    }

    private fun setCityName(value:String){
        stores_city_name.text = value
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

                if (stateMessage.response.message.equals(AccountStateEvent.GetUserDefaultCityEvent().toString())){
                    setCityName(
                        viewModel.getDefaultCity()!!.displayName
                    )
                    getStores()
                }
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->//must
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            recyclerStoresAdapter.submitList(
                viewModel.getStoresAround()
            )

            if(viewState != null){
                setLocationRadiusText(
                    viewModel.getRadius()
                )
            }
        })
    }

    private  fun resetUI(){
        stores_recyclerview.smoothScrollToPosition(0)
        uiCommunicationListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    @SuppressLint("SetTextI18n")
    private fun setLocationRadiusText(value:Double) {
        location_radius.text=BigDecimal(value).setScale(0,RoundingMode.UP).toString()
    }

    private fun getLocation() {
        val intent = LocationPicker.IntentBuilder()
            .setLatLong(viewModel.getDefaultCity()!!.lat, viewModel.getDefaultCity()!!.lon)
            .setMapRawResourceStyle(R.raw.store_view_map_style)
            .setMapType(MapType.NORMAL)
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
            .setInitialCircleRadiusKilometer(viewModel.getRadius())
            .setSliderValueFrom(1.0F)
            .setSliderValueTo(200.0F)
            .hideLocationButton(false)
            .enableMapGesturesEnabled(false)
            .build(requireActivity())

        startForResult.launch(intent)
    }

    val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val circleData = result.data?.getParcelableExtra<CircleData>(
                Constants.CIRCLE_INTENT)
            viewModel.apply {
                circleData?.let {
                    saveLocationInformation(circleData.radius)
                    setRadius(circleData.radius)
                }
            }
            getStores()
            resetUI()
        }
    }

    override fun onItemSelected(item: Store) {
        val dialog=
            StoreBottomSheetDialog(
                item.name,
                item.storeAddress.fullAddress,
                item.storeAddress.latitude,
                item.storeAddress.longitude
            )
        dialog.show(childFragmentManager,"dialog_store_bottom_sheet")
    }
}