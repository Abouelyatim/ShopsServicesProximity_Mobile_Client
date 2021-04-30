package com.smartcity.client.ui.main.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.Bill
import com.smartcity.client.models.OrderType
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.cart.state.CartStateEvent
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.ui.main.cart.viewmodel.*
import com.smartcity.client.util.Constants
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import com.smartcity.provider.models.SelfPickUpOptions
import kotlinx.android.synthetic.main.fragment_place_order.*
import javax.inject.Inject


class PlaceOrderFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCartFragment(R.layout.fragment_place_order)
{

    private var orderProductRecyclerAdapter: OrderProductAdapter?=null

    val viewModel: CartViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as CartViewState?)?.let { viewState ->
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
            CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY,
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
        stateChangeListener.expandAppBar()
        stateChangeListener.displayBottomNavigation(false)

        selectOptionBehavior()
        getStorePolicy()
        subscribeObservers()
        initProductsRecyclerView()
        setTotal()
        restoreUi()
    }

    private fun restoreUi() {
        when(viewModel.getOrderType()){
            OrderType.DELIVERY ->{
                option_delivery_background.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_order_option,null)
                option_self_pickup_background.background = ResourcesCompat.getDrawable(resources,R.drawable.default_order_option,null)
                displayPickupDescription(false)
                displayConfirmOrder()
            }
            OrderType.SELFPICKUP ->{
                option_self_pickup_background.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_order_option,null)
                option_delivery_background.background = ResourcesCompat.getDrawable(resources,R.drawable.default_order_option,null)
                displayPickupDescription(true)
                displayConfirmOrder()
            }
            else ->{

            }
        }
    }

    private fun getTotalToPay(orderType: OrderType) {
        viewModel.getStorePolicy()?.let {policy->
            viewModel.setStateEvent(
                CartStateEvent.GetTotalBill(
                    Bill(
                        policy.id,
                        getTotal(),
                        orderType
                    )
                )
            )
        }

    }

    private fun getTotal():Double {
        var total=0.0
        viewModel.getSelectedCartProduct()!!.cartProductVariants.map {
            total=it.productVariant.price*it.unit+total
        }
        return total
    }

    @SuppressLint("SetTextI18n")
    private fun setTotal() {
        order_product_total_price.text=getTotal().toString()+ Constants.DINAR_ALGERIAN
    }

    @SuppressLint("SetTextI18n")
    private fun setTotalToPay(total:Double) {
        order_product_total_to_pay_price.text=total.toString()+ Constants.DINAR_ALGERIAN
        order_total_price_.text=total.toString()+ Constants.DINAR_ALGERIAN
    }

    private fun setUpUi() {
        viewModel.getStorePolicy()?.let {policy->
            displayDeliveryOption(policy.delivery)
            pickupDescriptionText(policy.selfPickUpOption)
            pickupDescriptionTime(policy.validDuration)
        }
    }

    private fun pickupDescriptionTime(time:Long){
        time?.let {
            pickup_description_time.text=it.toString()
        }
    }

    private fun pickupDescriptionText(selfPickUpOptions: SelfPickUpOptions){
        var text=""
        when(selfPickUpOptions){
            SelfPickUpOptions.SELF_PICK_UP_TOTAL ->{
                text="To be able to reserve the articles of your order, the store  requires payment of the total amount."
            }

            SelfPickUpOptions.SELF_PICK_UP_PART_PERCENTAGE, SelfPickUpOptions.SELF_PICK_UP_PART_RANGE ->{
                text="To be able to reserve the articles of your order, the store requires the payment of a deposit."
            }

            SelfPickUpOptions.SELF_PICK_UP ->{
                text="This part of the order does not require any payment before getting it from the store"
            }

            SelfPickUpOptions.SELF_PICK_UP_EXTEND_PERCENTAGE -> {

            }
        }
        pickup_description_text.text=text
    }

    private fun displayConfirmOrder(){
        confirm_container.visibility=View.VISIBLE
    }

    private fun displayDeliveryOption(boolean: Boolean){
        if (boolean){
            option_delivery.visibility=View.VISIBLE
        }else{
            option_delivery.visibility=View.GONE
        }
    }

    private fun displayPickupDescription(boolean: Boolean){
        if (boolean){
            pickup_description.visibility=View.VISIBLE
        }else{
            pickup_description.visibility=View.GONE
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            if (dataState!=null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        when(response.message){

                            //set policy get it from network
                            SuccessHandling.DONE_STORE_POLICY ->{
                                data.data?.let{
                                    it.getContentIfNotHandled()?.let{
                                        it.orderFields.storePolicy?.let {
                                            viewModel.setStorePolicy(it)
                                        }
                                    }

                                }
                            }

                            SuccessHandling.DONE_TOTAL_BILL ->{
                                data.data?.let{
                                    it.getContentIfNotHandled()?.let{
                                        it.orderFields.total?.let {
                                            viewModel.setTotalBill(it)
                                        }
                                    }

                                }
                            }

                            else ->{

                            }

                        }
                    }
                }
            }


            setUpUi()
            viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
                viewModel.getTotalBill()?.let { bill->
                    setTotalToPay(bill.total!!)
                }
            })

        })
    }


    private fun getStorePolicy() {
        viewModel.getSelectedCartProduct()?.let {
            viewModel.setStateEvent(
                CartStateEvent.GetStorePolicy(
                    it.storeId
                )
            )
        }
    }

    private fun selectOptionBehavior(){
        option_delivery.setOnClickListener {
            viewModel.setOrderType(OrderType.DELIVERY)
            option_delivery_background.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_order_option,null)
            option_self_pickup_background.background = ResourcesCompat.getDrawable(resources,R.drawable.default_order_option,null)
            displayPickupDescription(false)
            getTotalToPay(OrderType.DELIVERY)
            displayConfirmOrder()
        }

        option_self_pickup.setOnClickListener {
            viewModel.setOrderType(OrderType.SELFPICKUP)
            option_self_pickup_background.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_order_option,null)
            option_delivery_background.background = ResourcesCompat.getDrawable(resources,R.drawable.default_order_option,null)
            displayPickupDescription(true)
            getTotalToPay(OrderType.SELFPICKUP)
            displayConfirmOrder()
        }
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

        orderProductRecyclerAdapter?.let {
            it.submitList(
                viewModel.getSelectedCartProduct()!!.cartProductVariants.sortedBy { it.productVariant.price }
            )
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        stateChangeListener.displayBottomNavigation(true)
    }
}