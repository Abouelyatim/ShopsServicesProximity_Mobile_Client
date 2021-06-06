package com.smartcity.client.ui.main.account.orders

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.Order
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.AccountViewModel
import com.smartcity.client.ui.main.account.viewmodel.getOrdersList
import com.smartcity.client.ui.main.account.viewmodel.setOrdersList
import com.smartcity.client.ui.main.account.viewmodel.setSelectedOrder
import com.smartcity.client.ui.main.cart.viewmodel.*
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_orders.*
import javax.inject.Inject

class OrdersFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_orders),
    OrdersAdapter.Interaction{

    private lateinit var recyclerOrdersAdapter: OrdersAdapter

    val viewModel: AccountViewModel by viewModels{
        viewModelFactory
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        stateChangeListener.expandAppBar()
        stateChangeListener.hideSoftKeyboard()
        stateChangeListener.displayBottomNavigation(false)

        initRecyclerView()
        subscribeObservers()
        getOrders()
    }

    private fun getOrders() {
        viewModel.setStateEvent(
            AccountStateEvent.GetUserOrdersEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)
            //set address list get it from network
            dataState.data?.let { data ->
                data.data?.let{
                    it.getContentIfNotHandled()?.let{
                        viewModel.setOrdersList(
                            it.orderFields.ordersList
                        )
                        //setEmptyListUi(it.addressList.isEmpty())
                    }
                }
            }

            if (dataState!=null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(response.message==SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE){
                            getOrders()
                        }
                    }
                }
            }
        })

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerOrdersAdapter.submitList(
                viewModel.getOrdersList()
            )
        })
    }

    fun initRecyclerView(){
        orders_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@OrdersFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerOrdersAdapter =
                OrdersAdapter(
                    requestManager,
                    this@OrdersFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = recyclerOrdersAdapter
        }

    }

    override fun selectedOrder(item: Order) {
        viewModel.setSelectedOrder(item)
        navViewOrder()
    }

    override fun confirmDelivery(order: Order) {
        viewModel.setStateEvent(
            AccountStateEvent.ConfirmOrderDeliveredEvent(
                order.id!!
            )
        )
    }

    override fun confirmPickUp(order: Order) {
        viewModel.setStateEvent(
            AccountStateEvent.ConfirmOrderPickedUpEvent(
                order.id!!
            )
        )
    }

    private fun navViewOrder() {
        findNavController().navigate(R.id.action_ordersFragment_to_viewOrderFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        //orders_recyclerview.adapter=null
    }
}