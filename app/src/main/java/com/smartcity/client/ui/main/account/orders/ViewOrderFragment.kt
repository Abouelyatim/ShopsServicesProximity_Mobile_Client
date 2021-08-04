package com.smartcity.client.ui.main.account.orders

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.Order
import com.smartcity.client.models.OrderType
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.getSelectedOrder
import com.smartcity.client.util.Constants
import com.smartcity.client.util.DateUtils.Companion.convertStringToStringDate
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_view_order.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ViewOrderFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_view_order,viewModelFactory){

    private  var  orderProductRecyclerAdapter: OrderProductAdapter?=null

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

        viewModel.getSelectedOrder()!!.apply {
            setOrderType(this)
            setStoreDetail(this)
            setOrderDetail(this)
            setReceiverDetail(this)
            setContentOrder(this)
            setBillOrder(this)
        }

    }

    private fun setOrderType(order:Order) {
        when(order.orderType){
            OrderType.DELIVERY ->{
                order_type.text="Delivery"
                order_type_delivery.visibility=View.VISIBLE
                order_store_address_container.visibility=View.GONE
            }

            OrderType.SELFPICKUP ->{
                order_type.text="Self pickup"
                order_type_self_pickup.visibility=View.VISIBLE
                order_delivery_address_container.visibility=View.GONE
            }
        }
    }

    private fun setStoreDetail(order:Order) {
        order_store_name.text=order.storeName
        order_store_address.text=order.storeAddress
    }

    @SuppressLint("SetTextI18n")
    private fun setOrderDetail(order:Order) {
        order_id.text=order.id.toString()
        order_date.text=convertStringToStringDate(order.createAt!!)
        order.address?.let {item->
            order_delivery_address.text=item.fullAddress
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setReceiverDetail(order:Order){
        order_receiver.text="${order.firstName} ${order.lastName} born in ${order.birthDay}"
    }

    private fun setContentOrder(order:Order){
        initProductsRecyclerView()
        orderProductRecyclerAdapter!!.submitList(order.orderProductVariants)
    }

    @SuppressLint("SetTextI18n")
    private fun setBillOrder(order:Order){
        order_total.text=order.bill!!.total.toString()+ Constants.DOLLAR
        order_paid.text=order.bill!!.alreadyPaid.toString()+ Constants.DOLLAR
        order_rest.text=(order.bill!!.total-order.bill!!.alreadyPaid).toString()+ Constants.DOLLAR
    }

    fun initProductsRecyclerView(){
        products_order_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            orderProductRecyclerAdapter =
                OrderProductAdapter(
                    requestManager
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = orderProductRecyclerAdapter
        }
    }
}