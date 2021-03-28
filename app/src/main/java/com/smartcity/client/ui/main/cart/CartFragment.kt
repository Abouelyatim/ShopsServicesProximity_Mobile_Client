package com.smartcity.client.ui.main.cart

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager


import com.smartcity.client.R
import com.smartcity.client.models.product.Cart
import com.smartcity.client.models.product.CartProductVariant
import com.smartcity.client.ui.AreYouSureCallback
import com.smartcity.client.ui.UIMessage
import com.smartcity.client.ui.UIMessageType
import com.smartcity.client.ui.displaySnackBar
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
            setTotalOrderPriceUi(
                calculateTotalPrice(
                    viewModel.getCartList()!!.cartProductVariants
                )
            )
            recyclerCartAdapter.submitList(
                generateProductCartList(
                    viewModel.getCartList()!!.cartProductVariants.sortedByDescending  { it.productVariant.price }
                )
            )
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setTotalOrderPriceUi(total:Double){
        cart_order_total_price.text=total.toString()+ Constants.DINAR_ALGERIAN
        cart_order_total_price_.text=total.toString()+ Constants.DINAR_ALGERIAN
    }

    private fun calculateTotalPrice(cartList: List<CartProductVariant>):Double{
        var total=0.0
        cartList.map {
            total=total+it.productVariant.price*it.unit
        }
        return total
    }
    private fun generateProductCartList(cartList:List<CartProductVariant>):Set<Cart>{
        val map:MutableMap<String,MutableList<CartProductVariant>> = mutableMapOf()
        cartList.map {productVariant->
            map.put(productVariant.storeName, mutableListOf())
        }
        cartList.map {productVariant->
            val list=map[productVariant.storeName]
            list!!.add(productVariant)
            map.put(productVariant.storeName,list)
        }

        val result:MutableList<Cart> = mutableListOf()
        map.map {
            result.add(Cart(it.value,it.key))
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

}

















