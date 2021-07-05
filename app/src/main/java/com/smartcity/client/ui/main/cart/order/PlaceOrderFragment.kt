package com.smartcity.client.ui.main.cart.order

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smartcity.client.R
import com.smartcity.client.models.*
import com.smartcity.client.models.product.OfferType
import com.smartcity.client.models.product.ProductVariants
import com.smartcity.client.ui.main.cart.BaseCartFragment
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.cart.state.CartStateEvent
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.ui.main.cart.viewmodel.*
import com.smartcity.client.util.Constants
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import com.smartcity.provider.models.SelfPickUpOptions
import kotlinx.android.synthetic.main.fragment_place_order.*
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject


class PlaceOrderFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCartFragment(R.layout.fragment_place_order)
{
    private lateinit var dialogView: View

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
        pickAddressListener()
        addUserInformationListener()


        place_order_button.setOnClickListener {
            showOrderConfirmationDialog()
        }
    }

    private fun placeOrder(){
        viewModel.setStateEvent(
            CartStateEvent.PlaceOrderEvent(
                Order(
                    id=-1,
                    storeId = viewModel.getSelectedCartProduct()!!.storeId,
                    orderType = viewModel.getOrderType()!!,
                    address = viewModel.getDeliveryAddress(),
                    cartProductVariantIds = viewModel.getSelectedCartProduct()!!.cartProductVariants.map { it.id },
                    firstName = viewModel.getUserInformation()!!.firstName,
                    lastName = viewModel.getUserInformation()!!.lastName,
                    birthDay = viewModel.getUserInformation()!!.birthDay,
                    bill = null,
                    createAt = "",
                    storeAddress = "",
                    storeName = "",
                    userId = -1,
                    validDuration = -1,
                    orderProductVariants = null,
                    orderState = null
                )
            )
        )
    }

    private fun restoreUi() {
        setDeliveryAddressUi(viewModel.getDeliveryAddress())
        when(viewModel.getOrderType()){
            OrderType.DELIVERY ->{
                option_delivery_background.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_order_option,null)
                option_self_pickup_background.background = ResourcesCompat.getDrawable(resources,R.drawable.default_order_option,null)
                displayPickupDescription(false)
                displayDeliveryAddress(true)
                displayUserInformation(true)
                displayConfirmOrder()
            }
            OrderType.SELFPICKUP ->{
                option_self_pickup_background.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_order_option,null)
                option_delivery_background.background = ResourcesCompat.getDrawable(resources,R.drawable.default_order_option,null)
                displayPickupDescription(true)
                displayDeliveryAddress(false)
                displayUserInformation(true)
                displayConfirmOrder()
            }
            else ->{

            }
        }
    }

    private fun getTotal():Double {
        var total=0.0
        viewModel.getSelectedCartProduct()!!.cartProductVariants.map {
            total += getPrice(it.productVariant) * it.unit
        }
        return total
    }

    private fun getPrice(productVariants: ProductVariants):Double {
        var prices = 0.0
        productVariants.let {
            val offer=it.offer
            if (offer!=null){
                when(offer.type){
                    OfferType.PERCENTAGE ->{
                        prices= BigDecimal(it.price-(it.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                    }

                    OfferType.FIXED ->{
                        prices=it.price-offer.newPrice!!
                    }
                    null -> {}
                }
            }else{
                prices=it.price
            }
        }

        return prices
    }

    private fun setUpUi() {
        viewModel.getStorePolicy()?.let {policy->
            displayDeliveryOption(policy.delivery)
            pickupDescriptionText(policy.selfPickUpOption)
            pickupDescriptionTime(policy.validDuration)
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

    private fun displayDeliveryAddress(boolean: Boolean){
        if (boolean){
            delivery_address.visibility=View.VISIBLE
        }else{
            delivery_address.visibility=View.GONE
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
                                            getUserAddresses()
                                        }
                                    }

                                }
                            }

                            SuccessHandling.DONE_USER_ADDRESSES ->{
                                data.data?.let{
                                    it.getContentIfNotHandled()?.let{
                                        it.orderFields.addressList?.let {
                                            viewModel.setAddressList(it)
                                            getUserInformation()
                                        }
                                    }
                                }
                            }

                            SuccessHandling.DONE_USER_INFORMATION ->{
                                data.data?.let{
                                    it.getContentIfNotHandled()?.let{
                                        it.orderFields.userInformation?.let {
                                            if(viewModel.getUserInformation()==null){
                                                viewModel.setUserInformation(it)
                                            }
                                        }
                                    }
                                }
                            }

                            SuccessHandling.DONE_PLACE_ORDER ->{
                                getUserCart()
                            }

                            SuccessHandling.DONE_USER_CART ->{
                                //set cart list get it from network
                                data.data?.let{
                                    it.getContentIfNotHandled()?.let{
                                        it.cartFields.cartList?.let {
                                            viewModel.setCartList(it)
                                        }
                                    }
                                    findNavController().popBackStack()
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


                viewModel.getAddressList().apply {
                    handelDeliveryAddressList(this)
                }

                viewModel.getUserInformation()?.let {userInformation ->
                    userInformation.birthDay?.plus(userInformation.firstName?.plus(userInformation.lastName?.let {
                        setUserInformation(userInformation)
                    }))

                }
            })

        })
    }

    private fun getUserCart() {
        viewModel.setStateEvent(
            CartStateEvent.GetUserCart()
        )
    }

    private fun handelDeliveryAddressList(list: List<Address>){
        if(list.isEmpty()){
            displayChangeDeliveryAddress(false)
            displayAddDeliveryAddress(true)
        }else{
            setDeliveryAddressUi(viewModel.getDeliveryAddress())
            displayChangeDeliveryAddress(true)
            displayAddDeliveryAddress(false)
        }
    }

    private fun pickAddressListener(){
        change_delivery_address.setOnClickListener {
            navPickAddress()
        }

        add_delivery_address.setOnClickListener {
            navAddAddress()
        }
    }

    private fun addUserInformationListener(){
        change_user_information.setOnClickListener {
            navUserInformation()
        }
    }

    private fun navAddAddress() {
        findNavController().navigate(R.id.action_placeOrderFragment_to_addAddressFragment)
    }

    private fun navPickAddress(){
        findNavController().navigate(R.id.action_placeOrderFragment_to_pickAddressFragment)
    }

    private fun navUserInformation(){
        findNavController().navigate(R.id.action_placeOrderFragment_to_addUserInformationFragment)
    }

    private fun displayChangeDeliveryAddress(boolean: Boolean){
        if (boolean){
            change_delivery_address.visibility=View.VISIBLE
        }else{
            change_delivery_address.visibility=View.GONE
        }
    }

    private fun displayAddDeliveryAddress(boolean: Boolean){
        if (boolean){
            add_delivery_address.visibility=View.VISIBLE
        }else{
            add_delivery_address.visibility=View.GONE
        }
    }

    private fun displayUserInformation(boolean: Boolean){
        if (boolean){
            user_information_container.visibility=View.VISIBLE
        }else{
            user_information_container.visibility=View.GONE
        }
    }

    private fun selectOptionBehavior(){
        option_delivery.setOnClickListener {
            viewModel.setOrderType(OrderType.DELIVERY)
            option_delivery_background.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_order_option,null)
            option_self_pickup_background.background = ResourcesCompat.getDrawable(resources,R.drawable.default_order_option,null)
            displayPickupDescription(false)
            displayDeliveryAddress(true)
            displayUserInformation(true)
            getTotalToPay(OrderType.DELIVERY)
            displayConfirmOrder()
        }

        option_self_pickup.setOnClickListener {
            viewModel.setOrderType(OrderType.SELFPICKUP)
            option_self_pickup_background.background = ResourcesCompat.getDrawable(resources,R.drawable.selected_order_option,null)
            option_delivery_background.background = ResourcesCompat.getDrawable(resources,R.drawable.default_order_option,null)
            displayPickupDescription(true)
            displayDeliveryAddress(false)
            displayUserInformation(true)
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

    private fun pickupDescriptionTime(time:Long){
        time?.let {
            pickup_description_time.text=it.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDeliveryAddressUi(address: Address?){
        address?.let {
            address_.text="${it.city}, ${it.street}, ${it.houseNumber.toString()}, ${it.zipCode.toString()}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTotal() {
        order_product_total_price.text=getTotal().toString()+ Constants.DOLLAR
    }

    @SuppressLint("SetTextI18n")
    private fun setTotalToPay(total:Double) {
        order_product_total_to_pay_price.text=total.toString()+ Constants.DOLLAR
        order_total_price_.text=total.toString()+ Constants.DOLLAR
    }

    @SuppressLint("SetTextI18n")
    private fun setUserInformation(userInformation: UserInformation) {
        user_information.text="${userInformation.firstName} ${userInformation.lastName} born in ${userInformation.birthDay}"
    }

    override fun onDestroy() {
        super.onDestroy()
        stateChangeListener.displayBottomNavigation(true)
    }

    private fun getTotalToPay(orderType: OrderType) {
        viewModel.getStorePolicy()?.let {policy->
            viewModel.setStateEvent(
                CartStateEvent.GetTotalBill(
                    BillTotal(
                        policy.id,
                        getTotal(),
                        orderType
                    )
                )
            )
        }

    }

    private fun getUserAddresses(){
        viewModel.setStateEvent(
            CartStateEvent.GetUserAddresses()
        )
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

    private fun getUserInformation(){
        viewModel.setStateEvent(
            CartStateEvent.GetUserInformation()
        )
    }

    @SuppressLint("SetTextI18n")
    private fun showOrderConfirmationDialog(){
        val dialog = BottomSheetDialog(context!!,R.style.BottomSheetDialogTheme)
        dialogView = layoutInflater.inflate(R.layout.dialog_order_confirmation, null)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)


        val total=dialogView.findViewById<TextView>(R.id.order_total)
        //total.text=calculateTotalPrice().toString()+Constants.DINAR_ALGERIAN

        val backButton=dialogView.findViewById<Button>(R.id.back_order)
        backButton.setOnClickListener {
            dialog.dismiss()
        }

        val confirmButton=dialogView.findViewById<Button>(R.id.confirm_order_button)
        confirmButton.setOnClickListener {
            placeOrder()
            dialog.dismiss()
        }

        dialog.show()


    }
}