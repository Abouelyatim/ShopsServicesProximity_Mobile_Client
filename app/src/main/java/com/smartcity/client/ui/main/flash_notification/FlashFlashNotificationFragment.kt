package com.smartcity.client.ui.main.flash_notification

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
import com.google.android.gms.maps.MapView
import com.smartcity.client.R
import com.smartcity.client.models.FlashDeal
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.main.flash_notification.OfferActionAdapter.Companion.getSelectedActionPositions
import com.smartcity.client.ui.main.flash_notification.OfferActionAdapter.Companion.setSelectedActionPositions
import com.smartcity.client.ui.main.flash_notification.state.CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.flash_notification.state.FlashStateEvent
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.ui.main.flash_notification.viewmodel.*
import com.smartcity.client.ui.main.product.products.ProductListAdapter
import com.smartcity.client.util.RightSpacingItemDecoration
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_flash_notification.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


@FlowPreview
@ExperimentalCoroutinesApi
class FlashFlashNotificationFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseFlashNotificationFragment(R.layout.fragment_flash_notification,viewModelFactory),
    ProductListAdapter.Interaction,
    OfferActionAdapter.Interaction,
    FlashDealsAdapter.Interaction
{
    private val days= mutableListOf<Pair<String,FlashDealsAdapter>>()
    private lateinit var pager :ViewPager
    private lateinit var pagerAdapter :MainPagerAdapter
    private val pagesNumber=15

    private lateinit var recyclerOfferActionAdapter: OfferActionAdapter
    private lateinit var productRecyclerAdapter: ProductListAdapter


    object ActionOffer {
        val FLASH = Pair<String,Int>("Flash",0)
        val DISCOUNT = Pair<String,Int>("Discount",1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY] as FlashViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    /**
     * !IMPORTANT!
     * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        uiCommunicationListener.displayBadgeBottomNavigationFlash(false)

        initDays()
        initPager()

        initProductRecyclerView()
        initOfferActionRecyclerView()
        subscribeObservers()
        setOfferAction()
        initData(viewModel.getOfferActionRecyclerPosition())
        getDefaultCity()
    }

    private fun getDefaultCity(){
        viewModel.setStateEvent(
            FlashStateEvent.GetUserDefaultCityEvent()
        )
    }

    private fun displayViewPager(boolean: Boolean){
        if(boolean){
            view_pager.visibility=View.VISIBLE
        }else{
            view_pager.visibility=View.GONE
        }
    }

    private fun displayProductDiscount(boolean: Boolean){
        if (boolean){
            discount_recyclerview.visibility=View.VISIBLE
        }else{
            discount_recyclerview.visibility=View.GONE
        }
    }

    private fun initPager() {
        pagerAdapter = MainPagerAdapter()
        pager = view_pager as ViewPager
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
            if(viewModel.getFlashDealsMap()[days[position].first]==null){
                getFlashDeals(days[position].first)
            }else{
                viewModel.getFlashDealsMap()[days[pager.currentItem].first]?.let {
                    days[pager.currentItem].second.submitList(it)
                }
            }
        }
    }

    private fun initDays() {
        val calendar = Calendar.getInstance()
        val formatter  = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        formatter.format(calendar.time)
        (0 .. pagesNumber).map {
            days.add(Pair(formatter.format(calendar.time),FlashDealsAdapter(this@FlashFlashNotificationFragment)))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        days.reverse()
    }

    private fun initData(actionPosition: Int) {
        when(actionPosition){
            ActionOffer.FLASH.second ->{
                displayViewPager(true)
                displayProductDiscount(false)
                getFlashDeals(days[pager.currentItem].first)
            }
            ActionOffer.DISCOUNT.second ->{
                displayViewPager(false)
                displayProductDiscount(true)
                getDiscountOrders()
            }
        }
    }

    private fun setOfferAction() {
        val list= mutableListOf<Triple<String,Int,Int>>()
        list.add(ActionOffer.FLASH.second,Triple(ActionOffer.FLASH.first, R.drawable.ic_outline_local_offer_white, R.drawable.ic_outline_local_offer_black))
        list.add(ActionOffer.DISCOUNT.second,Triple(ActionOffer.DISCOUNT.first,R.drawable.ic_outline_local_play_white, R.drawable.ic_outline_local_play_black))
        viewModel.setOfferActionList(
            list
        )
    }

    private fun initProductRecyclerView(){
        discount_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@FlashFlashNotificationFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productRecyclerAdapter =
                ProductListAdapter(
                    requestManager,
                    this@FlashFlashNotificationFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                }
            })
            adapter = productRecyclerAdapter
        }
    }

    private fun initOfferActionRecyclerView() {
        offer_action_recyclerview.apply {
            layoutManager = GridLayoutManager(this@FlashFlashNotificationFragment.context, 2, GridLayoutManager.VERTICAL, false)

            val rightSpacingDecorator = RightSpacingItemDecoration(0)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            recyclerOfferActionAdapter =
                OfferActionAdapter(
                    this@FlashFlashNotificationFragment
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

    private fun getFlashDeals(date:String) {
        viewModel.setStateEvent(
            FlashStateEvent.GetUserFlashDealsEvent(
                date
            )
        )
    }

    private fun getDiscountOrders() {
        viewModel.setStateEvent(
            FlashStateEvent.GetUserDiscountProductEvent()
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

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewModel.getDefaultCity()?.let {
                setCityText(it.displayName)
            }

            viewModel.getFlashDealsMap()[days[pager.currentItem].first]?.let {
                days[pager.currentItem].second.submitList(it)
            }

            productRecyclerAdapter.submitList(viewModel.getDiscountProductList(),false)

            recyclerOfferActionAdapter.apply {
                submitList(
                    viewModel.getOfferAction()
                )
            }
        })
    }

    private fun setCityText(value:String){
        offers_city_name.text=value
    }

    private fun initRecyclerView(recyclerView: RecyclerView,flashRecyclerAdapter:FlashDealsAdapter) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@FlashFlashNotificationFragment.context)
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

    override fun onActionItemSelected(position: Int, item: String) {
        offer_action_recyclerview.adapter!!.notifyDataSetChanged()
        resetUI()
        viewModel.setOfferActionRecyclerPosition(position)

        when(item){
            ActionOffer.FLASH.first ->{
                displayViewPager(true)
                displayProductDiscount(false)
                getFlashBehavior(pager.currentItem)
            }
            ActionOffer.DISCOUNT.first ->{
                displayViewPager(false)
                displayProductDiscount(true)
                getDiscountOrders()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setOfferActionRecyclerPosition(getSelectedActionPositions())
        recyclerOfferActionAdapter.resetSelectedActionPosition()

    }

    override fun onResume() {
        super.onResume()
        setSelectedActionPositions(viewModel.getOfferActionRecyclerPosition())
    }

    private  fun resetUI(){
       // flash_recyclerview.smoothScrollToPosition(0)
        discount_recyclerview.smoothScrollToPosition(0)
        uiCommunicationListener.hideSoftKeyboard()
    }

    override fun onItemSelected(position: Int, item: Product) {
        viewModel.setSelectedProduct(item)
        findNavController().navigate(R.id.action_flashFlashNotificationFragment_to_viewProductFlashFragment)
    }

    override fun restoreListPosition() {}


    override fun onItemSelected(item: FlashDeal) {
        showOrderConfirmationDialog(item)
    }

    private fun showOrderConfirmationDialog(flash: FlashDeal){
        val dialog=StoreBottomSheetDialog(flash)
        dialog.show(childFragmentManager,"dialog_store_bottom_sheet")
    }
}