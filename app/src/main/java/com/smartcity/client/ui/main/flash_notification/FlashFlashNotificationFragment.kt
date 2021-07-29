package com.smartcity.client.ui.main.flash_notification

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.main.blog.products.ProductListAdapter
import com.smartcity.client.ui.main.flash_notification.OfferActionAdapter.Companion.getSelectedActionPositions
import com.smartcity.client.ui.main.flash_notification.OfferActionAdapter.Companion.setSelectedActionPositions
import com.smartcity.client.ui.main.flash_notification.state.CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.flash_notification.state.FlashStateEvent
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.ui.main.flash_notification.viewmodel.*
import com.smartcity.client.util.RightSpacingItemDecoration
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_flash_notification.*
import javax.inject.Inject


class FlashFlashNotificationFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseFlashNotificationFragment(R.layout.fragment_flash_notification),
    ProductListAdapter.Interaction,
    OfferActionAdapter.Interaction
{

    private lateinit var recyclerOfferActionAdapter: OfferActionAdapter
    private lateinit var flashRecyclerAdapter: FlashDealsAdapter
    private lateinit var productRecyclerAdapter: ProductListAdapter

    object ActionOffer {
        val FLASH = Pair<String,Int>("Flash",0)
        val DISCOUNT = Pair<String,Int>("Discount",1)
    }

    val viewModel: FlashViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
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

    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        stateChangeListener.displayBadgeBottomNavigationFlash(false)

        initRecyclerView()
        initProductRecyclerView()
        initOfferActionRecyclerView()
        subscribeObservers()
        setOfferAction()
        initData(viewModel.getOfferActionRecyclerPosition())
    }

    private fun initData(actionPosition: Int) {
        when(actionPosition){
            ActionOffer.FLASH.second ->{
                viewModel.clearProductList()
                getFlashDeals()
            }
            ActionOffer.DISCOUNT.second ->{
                viewModel.clearFlashList()
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

    private fun getFlashDeals() {
        viewModel.setStateEvent(
            FlashStateEvent.GetUserFlashDealsEvent()
        )
    }

    private fun getDiscountOrders() {
        viewModel.setStateEvent(
            FlashStateEvent.GetUserDiscountProductEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            //set Offer list get it from network
            dataState.data?.let { data ->
                data.data?.let{
                    it.getContentIfNotHandled()?.let{
                        it.flashFields.flashDealsList.let {
                            viewModel.setFlashDealsList(it)
                        }

                        it.flashFields.productDiscountList.let {
                            viewModel.setDiscountProductList(it)
                        }
                    }

                }

            }
        })

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            flashRecyclerAdapter.submitList(viewModel.getFlashDealsList())
            productRecyclerAdapter.submitList(viewModel.getDiscountProductList(),false)

            recyclerOfferActionAdapter.apply {
                submitList(
                    viewModel.getOfferAction()
                )
            }
        })
    }

    private fun initRecyclerView() {
        flash_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@FlashFlashNotificationFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            flashRecyclerAdapter =
                FlashDealsAdapter(

                )
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
                viewModel.clearProductList()
                getFlashDeals()
            }
            ActionOffer.DISCOUNT.first ->{
                viewModel.clearFlashList()
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
        flash_recyclerview.smoothScrollToPosition(0)
        discount_recyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
    }

    override fun onItemSelected(position: Int, item: Product) {
        viewModel.setSelectedProduct(item)
        findNavController().navigate(R.id.action_flashFlashNotificationFragment_to_viewProductFlashFragment)
    }

    override fun restoreListPosition() {}
}