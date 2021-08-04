package com.smartcity.client.ui.main.account.stores

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.getCenterLatitude
import com.smartcity.client.ui.main.account.viewmodel.getCenterLongitude
import com.smartcity.client.ui.main.account.viewmodel.getRadius
import com.smartcity.client.ui.main.account.viewmodel.getStoresAround
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_around_stores.*
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
): BaseAccountFragment(R.layout.fragment_around_stores,viewModelFactory){

    private lateinit var recyclerStoresAdapter: StoresAdapter

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
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

        change_location.setOnClickListener {
            getLocation()
        }

        if (viewModel.getCenterLatitude()==0.0 && viewModel.getCenterLongitude()==0.0){
            getCurrentLocation()
            getLocation()
        }

        if(viewModel.getCenterLatitude()!=0.0 && viewModel.getCenterLongitude()!=0.0){
            getStores()
        }

        subscribeObservers()
        initRecyclerView()
    }

    fun initRecyclerView(){
        stores_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@AroundStoresFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerStoresAdapter =
                StoresAdapter(
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
            AccountStateEvent.GetStoresAroundEvent()
        )
    }

    private fun getCurrentLocation(){
       /* gpsTracker.getCurrentLocation()
        val latitude: Double = gpsTracker.getLatitude()
        val longitude: Double = gpsTracker.getLongitude()
        viewModel.setCenterLatitude(latitude)
        viewModel.setCenterLongitude(longitude)*/
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
       /* val intent = LocationPicker.IntentBuilder()
            .setLatLong(viewModel.getCenterLatitude(), viewModel.getCenterLongitude())
            .setDefaultMapZoom(9.0f)
            .setMapRawResourceStyle(R.raw.store_view_map_style)
            .setMapType(MapType.NORMAL)
            .setPlaceSearchBar(applicationInfo.metaData.getString("com.google.android.geo.API_KEY"))
            .setMarkerImageImageColor(R.color.bleu)
            .setFabColor(R.color.bleu)
            .setPrimaryTextColor(R.color.dark)
            .setSliderThumbTintColor(R.color.bleu)
            .setSliderTrackActiveTintColor(R.color.bleu)
            .setSliderTrackInactiveTintColor(R.color.grey3)
            .setInitialCircleRadiusKilometer(viewModel.getRadius())
            .setSliderValueFrom(1.0F)
            .setSliderValueTo(200.0F)
            .hideLocationButton(false)
            .build(activity!!)
        startActivityForResult(intent, Constants.PLACE_PICKER_REQUEST)*/
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
       /* if (requestCode == Constants.PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    val circleData = data?.getParcelableExtra<CircleData>(
                        Constants.CIRCLE_INTENT)
                    viewModel.apply {
                        circleData?.let {
                            saveLocationInformation(circleData.latitude, circleData.longitude, circleData.radius)
                            setCenterLatitude(circleData.latitude)
                            setCenterLongitude(circleData.longitude)
                            setRadius(circleData.radius)
                        }
                    }
                    getStores()
                    resetUI()
                } catch (e: Exception) {
                    Log.e("AroundStoresFragment", e.message)
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }*/
    }
}