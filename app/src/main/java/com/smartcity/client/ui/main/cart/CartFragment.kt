package com.smartcity.client.ui.main.cart

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.google.android.material.bottomsheet.BottomSheetDialog


import com.smartcity.client.R
import com.smartcity.client.models.product.Cart
import com.smartcity.client.models.product.CartProductVariant
import com.smartcity.client.ui.AreYouSureCallback
import com.smartcity.client.ui.UIMessage
import com.smartcity.client.ui.UIMessageType
import com.smartcity.client.ui.displaySnackBar
import com.smartcity.client.ui.main.blog.state.ProductStateEvent
import com.smartcity.client.ui.main.blog.viewProduct.adapters.ValuesAdapter
import com.smartcity.client.ui.main.blog.viewmodel.clearChoisesMap
import com.smartcity.client.ui.main.blog.viewmodel.getChoisesMap
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.cart.state.CartStateEvent
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.util.Constants
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_cart.*


import javax.inject.Inject

class CartFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCartFragment(R.layout.fragment_cart),
    CartAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{
    private lateinit var recyclerCartAdapter: CartAdapter
    private lateinit var dialogView: View

    val viewModel: CustomCategoryViewModel by viewModels{
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
        swipe_refresh.setOnRefreshListener(this)
       // stateChangeListener.expandAppBar()



        initvRecyclerView()
        subscribeObservers()
        getUserCart()

       /* place_order_button.setOnClickListener {
            showOrderConfirmationDialog()
        }*/
    }

    private fun getUserCart() {
        viewModel.setStateEvent(
            CartStateEvent.GetUserCart()
        )
    }


    fun initvRecyclerView(){
        cart_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@CartFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerCartAdapter =
                CartAdapter(
                    requestManager,
                    this@CartFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = recyclerCartAdapter
        }

    }

    fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            //delete Cart Product success
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(response.message.equals(SuccessHandling.DELETE_DONE)){
                            getUserCart()
                        }
                        if(response.message.equals(SuccessHandling.DONE_UPDATE_CART_QUANTITY)){
                            getUserCart()
                        }
                        if(response.message.equals(SuccessHandling.DONE_PLACE_ORDER)){
                            getUserCart()
                        }
                    }
                }
            }
            //set Custom Category list get it from network
            dataState.data?.let { data ->
                data.data?.let{
                    it.getContentIfNotHandled()?.let{
                        it.cartFields.cartList?.let {
                            viewModel.setCartList(it)
                        }
                    }

                }

            }
        })
        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerCartAdapter.submitList(
                generateProductCartList(
                    viewModel.getCartList()!!.cartProductVariants.sortedByDescending  { it.productVariant.price }
                )
            )
        })
    }



    private fun calculateTotalPrice():Double{
        viewModel.getCartList()?.let {
            var total=0.0
            it.cartProductVariants.map {
                total += it.productVariant.price * it.unit
            }
            return total
        }
        return 0.0
    }
    private fun generateProductCartList(cartList:List<CartProductVariant>):Set<Cart>{
        val map:MutableMap<Long,MutableList<CartProductVariant>> = mutableMapOf()
        cartList.map {productVariant->
            map.put(productVariant.storeId, mutableListOf())
        }
        cartList.map {productVariant->
            val list=map[productVariant.storeId]
            list!!.add(productVariant)
            map.put(productVariant.storeId,list)
        }

        val result:MutableList<Cart> = mutableListOf()
        map.map {
            result.add(Cart(it.value,it.key,it.value.first().storeName))
        }
        return result.toSet()
    }



    override fun onRefresh() {
        getUserCart()
        swipe_refresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        cart_recyclerview.adapter = null
        viewModel.clearCartList()
    }

    override fun addQuantity(variantId: Long, quantity: Int) {
        viewModel.setStateEvent(
            CartStateEvent.AddProductCartEvent(
                variantId,
                quantity
            )
        )
    }

    override fun deleteCartProduct(variantId: Long) {

        val callback: AreYouSureCallback = object: AreYouSureCallback {
            override fun proceed() {
                viewModel.setStateEvent(
                    CartStateEvent.DeleteProductCartEvent(variantId)
                )
            }
            override fun cancel() {
                // ignore
            }
        }
        uiCommunicationListener.onUIMessageReceived(
            UIMessage(
                getString(R.string.are_you_sure_delete),
                UIMessageType.AreYouSureDialog(callback)
            )
        )

    }

    @SuppressLint("SetTextI18n")
    private fun showOrderConfirmationDialog(){
        val dialog = BottomSheetDialog(context!!,R.style.BottomSheetDialogTheme)
        dialogView = layoutInflater.inflate(R.layout.dialog_order_confirmation, null)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)


        val total=dialogView.findViewById<TextView>(R.id.order_total)
        total.text=calculateTotalPrice().toString()+Constants.DINAR_ALGERIAN

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

    override fun placeOrder(item: Cart) {
        Log.d("ii",item.storeName)
        showOrderConfirmationDialog()
    }

    private fun placeOrder(){
        viewModel.setStateEvent(
            CartStateEvent.PlaceOrderEvent(
                viewModel.getCartList()
            )
        )
    }
}
















