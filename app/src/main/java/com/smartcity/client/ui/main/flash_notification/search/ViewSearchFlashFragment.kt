package com.smartcity.client.ui.main.flash_notification.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.FlashDeal
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.main.flash_notification.BaseFlashNotificationFragment
import com.smartcity.client.ui.main.flash_notification.flash.*
import com.smartcity.client.ui.main.flash_notification.state.CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.flash_notification.state.FlashStateEvent
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.ui.main.flash_notification.viewmodel.*
import com.smartcity.client.ui.main.product.products.ProductListAdapter
import com.smartcity.client.util.RightSpacingItemDecoration
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_view_search_flash.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ViewSearchFlashFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseFlashNotificationFragment(R.layout.fragment_view_search_flash,viewModelFactory),
    FlashDealsAdapter.Interaction,
    ProductListAdapter.Interaction,
    OfferActionAdapter.Interaction
{

    private val days= mutableListOf<Pair<String, FlashDealsAdapter>>()
    private lateinit var pager : ViewPager
    private lateinit var pagerAdapter : MainPagerAdapter
    private val pagesNumber=15

    private lateinit var recyclerOfferActionAdapter: OfferActionAdapter
    private lateinit var productRecyclerAdapter: ProductListAdapter

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

        initDays()
        initPager()

        initProductRecyclerView()
        initOfferActionRecyclerView()
        subscribeObservers()
        initData(viewModel.getSearchOfferActionRecyclerPosition())
        backProceed()
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initData(actionPosition: Int) {
        when(actionPosition){
            FlashFlashNotificationFragment.ActionOffer.FLASH.second ->{
                displayViewPager(true)
                displayProductDiscount(false)
                getFlashDeals(days[pager.currentItem].first)
            }
            FlashFlashNotificationFragment.ActionOffer.DISCOUNT.second ->{
                displayViewPager(false)
                displayProductDiscount(true)
                getDiscountOrders()
            }
        }
    }

    private fun getDiscountOrders() {
        viewModel.setStateEvent(
            FlashStateEvent.SearchDiscountProductEvent()
        )
    }

    private fun displayViewPager(boolean: Boolean){
        if(boolean){
            search_view_pager.visibility=View.VISIBLE
        }else{
            search_view_pager.visibility=View.GONE
        }
    }

    private fun displayProductDiscount(boolean: Boolean){
        if (boolean){
            search_discount_recyclerview.visibility=View.VISIBLE
        }else{
            search_discount_recyclerview.visibility=View.GONE
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

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->

            viewModel.getSearchFlashDealsMap()[days[pager.currentItem].first]?.let {
                days[pager.currentItem].second.submitList(it)
            }

            productRecyclerAdapter.submitList(
                viewModel.getSearchDiscountProductList(),
                false
            )

            recyclerOfferActionAdapter.apply {
                submitList(
                    viewModel.getOfferAction()
                )
            }
        })
    }

    private fun initOfferActionRecyclerView() {
        search_offer_action_recyclerview.apply {
            layoutManager = GridLayoutManager(this@ViewSearchFlashFragment.context, 2, GridLayoutManager.VERTICAL, false)

            val rightSpacingDecorator = RightSpacingItemDecoration(0)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            recyclerOfferActionAdapter =
                OfferActionAdapter(
                    this@ViewSearchFlashFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            recyclerOfferActionAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerOfferActionAdapter
        }
    }

    private fun initProductRecyclerView() {
        search_discount_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ViewSearchFlashFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productRecyclerAdapter =
                ProductListAdapter(
                    requestManager,
                    this@ViewSearchFlashFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                }
            })
            adapter = productRecyclerAdapter
        }
    }

    private fun initPager() {
        pagerAdapter =
            MainPagerAdapter()
        pager = search_view_pager as ViewPager
        pager.adapter = pagerAdapter

        val inflater: LayoutInflater = requireActivity().layoutInflater
        (0 .. pagesNumber).map {
            val layout = inflater.inflate(R.layout.layout_flash_day_page, null) as FrameLayout
            pagerAdapter.addView(layout, it)
            val title=layout.findViewById<TextView>(R.id.flash_day)
            title.text = days[it].first
            val recyclerView = layout.findViewById<RecyclerView>(R.id.flash_recyclerview)
            initRecyclerView(recyclerView, days[it].second)
        }

        pager.currentItem = days.size
        pagerAdapter.notifyDataSetChanged()

        pager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                getFlashBehavior(position)
            }
        })
    }

    private fun getFlashBehavior(position: Int){
        if(position==pagesNumber){//all ways get from network today flash because we still receive new flashes
            getFlashDeals(days[position].first)
        }else{
            if(viewModel.getSearchFlashDealsMap()[days[position].first]==null){
                getFlashDeals(days[position].first)
            }else{
                viewModel.getSearchFlashDealsMap()[days[pager.currentItem].first]?.let {
                    days[pager.currentItem].second.submitList(it)
                }
            }
        }
    }

    private fun getFlashDeals(date:String) {
        viewModel.setStateEvent(
            FlashStateEvent.SearchFlashDealsEvent(
                date
            )
        )
    }

    private fun initRecyclerView(recyclerView: RecyclerView,flashRecyclerAdapter: FlashDealsAdapter) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ViewSearchFlashFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = flashRecyclerAdapter
        }
    }

    private fun initDays() {
        val calendar = Calendar.getInstance()
        val formatter  = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        formatter.format(calendar.time)
        (0 .. pagesNumber).map {
            days.add(Pair(formatter.format(calendar.time),
                FlashDealsAdapter(
                    this@ViewSearchFlashFragment
                )
            ))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        days.reverse()
    }

    override fun onResume() {
        super.onResume()
        OfferActionAdapter.setSelectedActionPositions(viewModel.getSearchOfferActionRecyclerPosition())
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setSearchOfferActionRecyclerPosition(OfferActionAdapter.getSelectedActionPositions())
        recyclerOfferActionAdapter.resetSelectedActionPosition()

    }

    override fun onItemSelected(item: FlashDeal) {
        showStoreDetailsDialog(item)
    }

    private fun showStoreDetailsDialog(flash: FlashDeal){
        val dialog=
            StoreBottomSheetDialog(
                flash.storeName!!,
                flash.storeAddress!!,
                flash.latitude,
                flash.longitude
            )
        dialog.show(childFragmentManager,"dialog_store_bottom_sheet")
    }

    override fun onItemSelected(position: Int, item: Product) {
        //todo
    }

    override fun restoreListPosition() {

    }

    private  fun resetUI(){
        search_discount_recyclerview.smoothScrollToPosition(0)
        uiCommunicationListener.hideSoftKeyboard()
    }

    override fun onActionItemSelected(position: Int, item: String) {
        search_offer_action_recyclerview.adapter!!.notifyDataSetChanged()
        resetUI()
        viewModel.setOfferActionRecyclerPosition(position)

        when(item){
            FlashFlashNotificationFragment.ActionOffer.FLASH.first ->{
                displayViewPager(true)
                displayProductDiscount(false)
                getFlashBehavior(pager.currentItem)
            }
            FlashFlashNotificationFragment.ActionOffer.DISCOUNT.first ->{
                displayViewPager(false)
                displayProductDiscount(true)
                getDiscountOrders()
            }
        }
    }
}