package com.smartcity.client.ui.main.cart.cart


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.product.Cart
import com.smartcity.client.models.product.CartProductVariant
import com.smartcity.client.ui.AreYouSureCallback
import com.smartcity.client.ui.main.cart.BaseCartFragment
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.cart.state.CartStateEvent
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.ui.main.cart.viewmodel.clearOrderFields
import com.smartcity.client.ui.main.cart.viewmodel.getCartList
import com.smartcity.client.ui.main.cart.viewmodel.setSelectedCartProduct
import com.smartcity.client.util.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class CartFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCartFragment(R.layout.fragment_cart,viewModelFactory),
    CartAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{
    private lateinit var recyclerCartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            CartStateEvent.GetUserCartEvent()
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
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.DELETE_DONE)){
                    getUserCart()
                }

                if(stateMessage.response.message.equals(SuccessHandling.DONE_UPDATE_CART_QUANTITY)){
                    getUserCart()
                }

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

            viewModel.getCartList()?.let {
                setEmptyListUi(it.cartProductVariants.isEmpty())
            }


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
        //viewModel.clearCartList()
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
        uiCommunicationListener.onResponseReceived(
            response = Response(
                message = getString(R.string.are_you_sure_delete),
                uiComponentType = UIComponentType.AreYouSureDialog(callback),
                messageType = MessageType.Info()
            ),
            stateMessageCallback = object: StateMessageCallback{
                override fun removeMessageFromStack() {
                    viewModel.clearStateMessage()
                }
            }
        )
    }

    override fun placeOrder(item: Cart) {
        viewModel.clearOrderFields()
        viewModel.setSelectedCartProduct(item)
        navPlaceOrder()
    }

    private fun navPlaceOrder(){
        findNavController().navigate(R.id.action_customCategoryFragment_to_placeOrderFragment)
    }

    private fun setEmptyListUi(empty:Boolean){
        if(empty){
            empty_list.visibility=View.VISIBLE
        }else{
            empty_list.visibility=View.GONE
        }
    }
}

















