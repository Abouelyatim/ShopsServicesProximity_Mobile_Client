package com.smartcity.client.ui.main.blog.search

import android.os.Bundle
import android.util.Log
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
import com.smartcity.client.ui.deleted.DataState
import com.smartcity.client.ui.main.blog.products.ProductListAdapter
import com.smartcity.client.ui.main.blog.products.BaseBlogFragment
import com.smartcity.client.ui.main.blog.products.ProductGridAdapter
import com.smartcity.client.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.viewmodel.ProductViewModel
import com.smartcity.client.ui.main.blog.viewmodel.getGridOrListViewSearch
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import handleIncomingProductSearchListData
import kotlinx.android.synthetic.main.fragment_search_product.*
import loadFirstSearchPageSearch
import nextPageSearch
import javax.inject.Inject


class SearchProductFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseBlogFragment(R.layout.fragment_search_product),
    ProductListAdapter.Interaction,
    ProductGridAdapter.Interaction
{
    private lateinit var productListRecyclerAdapter: ProductListAdapter
    private lateinit var productGridRecyclerAdapter: ProductGridAdapter

    val viewModel: ProductViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[BLOG_VIEW_STATE_BUNDLE_KEY] as ProductViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    /**
     * !IMPORTANT!
     * Must save ViewState b/c in event of process death the LiveData in ViewModel will be lost
     */
    override fun onSaveInstanceState(outState: Bundle) {
        val viewState = viewModel.viewState.value

        //clear the list. Don't want to save a large list to bundle.
        // viewState?.productFields?.productList = ArrayList()

        outState.putParcelable(
            BLOG_VIEW_STATE_BUNDLE_KEY,
            viewState
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

        stateChangeListener.displayBottomNavigation(false)
        stateChangeListener.displayAppBar(true)
        stateChangeListener.setAppBarLayout(
            SearchAppBarFragment(
                viewModelFactory
            )
        )
        stateChangeListener.updateStatusBarColor(R.color.white,false)

        setSelectedRecyclerView(viewModel.getGridOrListViewSearch())
        initProductGridRecyclerView()
        initProductListRecyclerView()
        subscribeObservers()
    }

    private fun initProductGridRecyclerView(){
        search_product_grid_recyclerview.apply {
            layoutManager = GridLayoutManager(this@SearchProductFragment.context, 2, GridLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productGridRecyclerAdapter =
                ProductGridAdapter(
                    requestManager,
                    this@SearchProductFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == productGridRecyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
                        viewModel.nextPageSearch()
                    }
                }
            })
            productGridRecyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = productGridRecyclerAdapter

        }
    }

    private fun initProductListRecyclerView(){
        search_product_list_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@SearchProductFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productListRecyclerAdapter =
                ProductListAdapter(
                    requestManager,
                    this@SearchProductFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == productListRecyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
                        viewModel.nextPageSearch()
                    }
                }
            })
            productListRecyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = productListRecyclerAdapter
        }
    }

    private fun setSelectedRecyclerView(boolean: Boolean){
        if(boolean){
            search_product_grid_recyclerview.visibility=View.GONE
            search_product_list_recyclerview.visibility=View.VISIBLE
        }else{
            search_product_list_recyclerview.visibility=View.GONE
            search_product_grid_recyclerview.visibility=View.VISIBLE
        }
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)

            if(dataState != null) {
                handlePagination(dataState)
            }

            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->

                        if(!data.response.hasBeenHandled){
                            when(data.response.getContentIfNotHandled()?.message){

                                SuccessHandling.DONE_Product_Layout_Change_Event ->{
                                    resetUI()
                                    setSelectedRecyclerView(viewModel.getGridOrListViewSearch())
                                }

                                SuccessHandling.DONE_Back_Clicked_Store_Event ->{
                                    findNavController().popBackStack()
                                }

                                SuccessHandling.DONE_Search_Event ->{
                                    onProductSearch()
                                }
                            }
                        }


                    }
                }
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            Log.d(TAG, "BlogFragment, ViewState: ${viewState}")
            if(viewState != null){
                productGridRecyclerAdapter.apply {
                    submitList(
                        productList = viewState.searchProductFields.productSearchList.distinct(),
                        isQueryExhausted = viewState.searchProductFields.isQuerySearchExhausted
                    )
                }

                productListRecyclerAdapter.apply {
                    submitList(
                        productList = viewState.searchProductFields.productSearchList.distinct(),
                        isQueryExhausted = viewState.searchProductFields.isQuerySearchExhausted
                    )
                }
            }
        })
    }

    fun onProductSearch(){
        viewModel.loadFirstSearchPageSearch().let {
            resetUI()
        }
    }

    private  fun resetUI(){
        search_product_list_recyclerview.smoothScrollToPosition(0)
        search_product_grid_recyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    private fun handlePagination(dataState: DataState<ProductViewState>){
        // Handle incoming data from DataState
        dataState.data?.let { data ->
            data.data?.let{
                it.peekContent().let{
                    viewModel.handleIncomingProductSearchListData(it)
                }
            }
        }
    }

    override fun onItemSelected(position: Int, item: Product) {

    }

    override fun restoreListPosition() {

    }

    override fun onGridItemSelected(position: Int, item: Product) {

    }

    override fun restoreGridListPosition() {

    }
}