package com.smartcity.client.ui.main.blog.store

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
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
import com.smartcity.client.ui.main.blog.BaseBlogFragment
import com.smartcity.client.ui.main.blog.state.ProductStateEvent
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.store.ViewCustomCategoryAdapter.Companion.getSelectedPositions
import com.smartcity.client.ui.main.blog.store.ViewCustomCategoryAdapter.Companion.setSelectedPositions
import com.smartcity.client.ui.main.blog.viewProduct.ViewProductAppBarFragment
import com.smartcity.client.ui.main.blog.viewmodel.*
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.util.RightSpacingItemDecoration
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_store.*
import javax.inject.Inject


class StoreFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseBlogFragment(R.layout.fragment_store),
    ViewCustomCategoryAdapter.Interaction,
    ViewCustomCategoryAdapter.InteractionAll,
    ViewProductStoreAdapter.Interaction
{
    private lateinit var customCategoryrecyclerAdapter: ViewCustomCategoryAdapter
    private lateinit var productRecyclerAdapter: ViewProductStoreAdapter

    val viewModel: ProductViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as ProductViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }
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
        setHasOptionsMenu(true)
        stateChangeListener.expandAppBar()
        stateChangeListener.displayAppBar(true)
        stateChangeListener.displayBottomNavigation(false)
        stateChangeListener.setAppBarLayout(
            StoreAppBarFragment(
                viewModelFactory
            )
        )
        stateChangeListener.updateStatusBarColor(R.color.white,false)

        initCustomCategoryRecyclerView()
        initProductRecyclerView()
        subscribeObservers()
        setStoreName()
        setStoreFollowers()
        handleFollowings()

        if(viewModel.getCustomCategoryRecyclerPosition()==0){
            getStoreCustomCategory(
                viewModel.getViewProductFields()!!.storeId
            )
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
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)

            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(!data.response.hasBeenHandled){
                            when(data.response.getContentIfNotHandled()?.message){
                                SuccessHandling.DONE_Back_Clicked_Store_Event ->{
                                    findNavController().popBackStack()
                                }
                            }
                        }
                    }
                }
            }

            if(dataState != null){
                //set Product list get it from network
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        when(response.message){

                            SuccessHandling.DONE_Get_Products_By_Custom_Category->{
                                data.data?.let{
                                    it.peekContent()?.let{
                                        it.viewProductFields.storeProductList.let {
                                            viewModel.setStoreProductLists(it)
                                        }
                                    }

                                }
                            }

                            SuccessHandling.DONE_Follow_Store ->{
                                store_stop_follow_button.visibility=View.VISIBLE
                                store_follow_button.visibility=View.GONE
                            }

                            SuccessHandling.DONE_Stop_Following_Store ->{
                                store_stop_follow_button.visibility=View.GONE
                                store_follow_button.visibility=View.VISIBLE
                            }

                            SuccessHandling.is_Following ->{
                                getAllStoreProducts(
                                    viewModel.getViewProductFields()!!.storeId
                                )
                                store_stop_follow_button.visibility=View.VISIBLE
                            }

                            SuccessHandling.not_Following ->{
                                getAllStoreProducts(
                                    viewModel.getViewProductFields()!!.storeId
                                )
                                store_follow_button.visibility=View.VISIBLE
                            }

                            SuccessHandling.DONE_Get_Store_Custom_Category->{
                                isFollowingStore()
                                data.data?.let{
                                    it.peekContent()?.let{
                                        it.viewProductFields.storeCustomCategoryList.let {
                                            viewModel.setStoreCustomCategoryLists(it)
                                        }
                                    }

                                }
                            }

                            SuccessHandling.DONE_Get_All_Products_By_Store->{
                                data.data?.let{
                                    it.peekContent()?.let{
                                        it.viewProductFields.storeProductList.let {
                                            viewModel.setStoreProductLists(it)
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