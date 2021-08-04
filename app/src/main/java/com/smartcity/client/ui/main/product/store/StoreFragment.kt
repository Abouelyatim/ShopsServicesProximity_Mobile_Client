package com.smartcity.client.ui.main.product.store

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.smartcity.client.R
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.product.BaseProductFragment
import com.smartcity.client.ui.main.product.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.product.state.ProductStateEvent
import com.smartcity.client.ui.main.product.state.ProductViewState
import com.smartcity.client.ui.main.product.store.ViewCustomCategoryAdapter.Companion.getSelectedPositions
import com.smartcity.client.ui.main.product.store.ViewCustomCategoryAdapter.Companion.setSelectedPositions
import com.smartcity.client.ui.main.product.viewmodel.*
import com.smartcity.client.util.RightSpacingItemDecoration
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_store.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class StoreFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseProductFragment(R.layout.fragment_store,viewModelFactory),
    ViewCustomCategoryAdapter.Interaction,
    ViewCustomCategoryAdapter.InteractionAll,
    ViewProductStoreAdapter.Interaction
{
    private lateinit var customCategoryrecyclerAdapter: ViewCustomCategoryAdapter
    private lateinit var productRecyclerAdapter: ViewProductStoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[BLOG_VIEW_STATE_BUNDLE_KEY] as ProductViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            BLOG_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.displayBottomNavigation(false)
        uiCommunicationListener.updateStatusBarColor(R.color.white,false)

        initCustomCategoryRecyclerView()
        initProductRecyclerView()
        subscribeObservers()
        setStoreName()
        setStoreFollowers()
        handleFollowings()

        getAllStoreProducts(
            viewModel.getViewProductFields()!!.storeId
        )
        isFollowingStore()

        if(viewModel.getCustomCategoryRecyclerPosition()==0){
            getStoreCustomCategory(
                viewModel.getViewProductFields()!!.storeId
            )
        }

        onBackClicked()
    }

    private fun onBackClicked() {
        back_button.setOnClickListener {
           findNavController().popBackStack()
        }
    }

    private fun handleFollowings() {
        store_follow_button.setOnClickListener {
            viewModel.setStateEvent(
                ProductStateEvent.FollowStoreEvent(
                    viewModel.getViewProductFields()!!.storeId
                )
            )
        }

        store_stop_follow_button.setOnClickListener {
            viewModel.setStateEvent(
                ProductStateEvent.StopFollowingStoreEvent(
                    viewModel.getViewProductFields()!!.storeId
                )
            )
        }
    }

    private fun setStoreFollowers() {
        store_followers.text=viewModel.getViewProductFields()!!.storeFollowers.toString()
    }

    private fun setStoreName() {
        store_name.text=viewModel.getViewProductFields()!!.storeName

    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.DONE_Follow_Store)){
                    store_stop_follow_button.visibility=View.VISIBLE
                    store_follow_button.visibility=View.GONE
                }

                if(stateMessage.response.message.equals(SuccessHandling.DONE_Stop_Following_Store)){
                    store_stop_follow_button.visibility=View.GONE
                    store_follow_button.visibility=View.VISIBLE
                }

                if(stateMessage.response.message.equals(SuccessHandling.is_Following)){
                    store_stop_follow_button.visibility=View.VISIBLE
                }

                if(stateMessage.response.message.equals(SuccessHandling.not_Following)){
                    store_follow_button.visibility=View.VISIBLE
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

            customCategoryrecyclerAdapter.submitList(viewModel.getStoreCustomCategoryLists())
            productRecyclerAdapter.apply {
                submitList(viewModel.getStoreProductLists())
            }
        })
    }

    private fun isFollowingStore(){
        viewModel.setStateEvent(
            ProductStateEvent.IsFollowingStoreEvent(
                viewModel.getViewProductFields()!!.storeId
            )
        )
    }

    private fun initProductRecyclerView() {
        view_product_recyclerview.apply {
            layoutManager = GridLayoutManager(this@StoreFragment.context, 2, GridLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productRecyclerAdapter =
                ViewProductStoreAdapter(
                    requestManager,
                    this@StoreFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()


                }
            })

            productRecyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = productRecyclerAdapter

        }
    }

    private fun initCustomCategoryRecyclerView() {
        view_custom_category_recyclerview.apply {
            layoutManager = FlexboxLayoutManager(context)
            (layoutManager as FlexboxLayoutManager).justifyContent = JustifyContent.FLEX_START
            (layoutManager as FlexboxLayoutManager).flexWrap= FlexWrap.WRAP

            val rightSpacingDecorator = RightSpacingItemDecoration(16)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            customCategoryrecyclerAdapter =
                ViewCustomCategoryAdapter(
                    this@StoreFragment,
                    this@StoreFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            customCategoryrecyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = customCategoryrecyclerAdapter

        }
    }

    fun getStoreCustomCategory(storeId:Long){
        viewModel.setStateEvent(
            ProductStateEvent.GetStoreCustomCategoryEvent(
                storeId
            )
        )
    }

    fun getProductsByCustomCategory(customCategoryId:Long){
        viewModel.setStateEvent(
            ProductStateEvent.GetProductsByCustomCategoryEvent(
                customCategoryId
            )
        )
        viewModel.clearStoreProductLists()
    }

    fun getAllStoreProducts(storeId:Long){
        viewModel.setStateEvent(
            ProductStateEvent.GetAllStoreProductsEvent(
                storeId
            )
        )
        viewModel.clearStoreProductLists()
    }

    override fun onItemSelected(position: Int, item: CustomCategory) {
        view_custom_category_recyclerview.adapter!!.notifyDataSetChanged()
        getProductsByCustomCategory(
            item.pk.toLong()
        )
    }

    override fun onItemSelected(item: Product) {
        viewModel.setViewProductFields(item)
        findNavController().navigate(R.id.action_storeFragment_to_viewProductFragment)
    }

    override fun onItemAllSelected() {
        view_custom_category_recyclerview.adapter!!.notifyDataSetChanged()
        getAllStoreProducts(
            viewModel.getViewProductFields()!!.storeId
        )
    }

    override fun onResume() {
        super.onResume()
        setSelectedPositions(viewModel.getCustomCategoryRecyclerPosition())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        viewModel.setCustomCategoryRecyclerPosition(getSelectedPositions())
        customCategoryrecyclerAdapter.resetSelectedPosition()
        view_custom_category_recyclerview.adapter = null
        view_product_recyclerview.adapter=null
    }
}