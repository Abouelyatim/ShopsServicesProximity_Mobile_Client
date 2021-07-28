package com.smartcity.client.ui.main.blog.products

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.main.blog.ProductListAdapter
import com.smartcity.client.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.viewmodel.*
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import handleIncomingBlogListData
import kotlinx.android.synthetic.main.fragment_blog.*
import loadFirstPage
import loadProductMainList
import nextPage
import javax.inject.Inject

class BlogFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseBlogFragment(R.layout.fragment_blog),
    ProductListAdapter.Interaction,
    ProductGridAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{
    val viewModel: ProductViewModel by viewModels{
        viewModelFactory
    }

    private lateinit var productListRecyclerAdapter: ProductListAdapter
    private lateinit var productGridRecyclerAdapter: ProductGridAdapter

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
        swipe_refresh.setOnRefreshListener(this)
        stateChangeListener.displayBottomNavigation(true)
        stateChangeListener.displayAppBar(true)
        stateChangeListener.setAppBarLayout(
            ProductAppBarFragment(
                viewModelFactory
            )
        )
        stateChangeListener.updateStatusBarColor(R.color.white,false)


        setSelectedRecyclerView(viewModel.getGridOrListView())
        initProductGridRecyclerView()
        initProductListRecyclerView()
        subscribeObservers()

        if(viewModel.getProductList().isEmpty()){
            loadProductMainList()
        }
    }

    private fun setSelectedRecyclerView(boolean: Boolean){
        if(boolean){
            product_grid_recyclerview.visibility=View.GONE
            product_list_recyclerview.visibility=View.VISIBLE
        }else{
            product_list_recyclerview.visibility=View.GONE
            product_grid_recyclerview.visibility=View.VISIBLE
        }
    }

    fun loadProductMainList(){
        viewModel.loadProductMainList()
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)

            if(dataState != null) {
                handlePagination(dataState)
            }

            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                       if(!data.response.hasBeenHandled){
                           when(response.message){
                               SuccessHandling.DONE_Product_Layout_Change_Event ->{
                                   resetUI()
                                   setSelectedRecyclerView(viewModel.getGridOrListView())
                               }

                               SuccessHandling.DONE_search_Product_Event ->{
                                   navSearch()
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
                        productList = viewState.productFields.productList.distinct(),
                        isQueryExhausted = viewState.productFields.isQueryExhausted
                    )
                }

                productListRecyclerAdapter.apply {
                    submitList(
                        productList = viewState.productFields.productList.distinct(),
                        isQueryExhausted = viewState.productFields.isQueryExhausted
                    )
                }
            }
        })
    }

    private  fun resetUI(){
        product_list_recyclerview.smoothScrollToPosition(0)
        product_grid_recyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    private fun handlePagination(dataState: DataState<ProductViewState>){

        // Handle incoming data from DataState
        dataState.data?.let { data ->
            data.data?.let{
                it.peekContent().let{
                    viewModel.handleIncomingBlogListData(it)
                }
            }
        }
    }

    private fun initProductGridRecyclerView(){
        product_grid_recyclerview.apply {
            layoutManager = GridLayoutManager(this@BlogFragment.context, 2, GridLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productGridRecyclerAdapter =
                ProductGridAdapter(
                    requestManager,
                    this@BlogFragment
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
                        viewModel.nextPage()
                    }
                }
            })
            productGridRecyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = productGridRecyclerAdapter

        }
    }

    private fun initProductListRecyclerView(){
        product_list_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productListRecyclerAdapter =
                ProductListAdapter(
                    requestManager,
                    this@BlogFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == productListRecyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
                        viewModel.nextPage()
                    }
                }
            })
            productListRecyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = productListRecyclerAdapter
        }
    }

    override fun onItemSelected(position: Int, item: Product) {
        viewModel.setViewProductFields(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewProductFragment)
    }

    override fun restoreListPosition() {
       
    }

    override fun onGridItemSelected(position: Int, item: Product) {
        viewModel.setViewProductFields(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewProductFragment)
    }

    override fun restoreGridListPosition() {
       
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        product_list_recyclerview.adapter = null
        product_grid_recyclerview.adapter = null
       // viewModel.clearProductListData()
    }

    fun onProductMain(){
        viewModel.loadFirstPage().let {
            resetUI()
        }
    }
    override fun onRefresh() {
        onProductMain()
        swipe_refresh.isRefreshing = false
    }


    override fun onPause() {
        super.onPause()
        
    }

    override fun onResume() {
        super.onResume()
        viewModel.clearStoreInformation()
        viewModel.clearProductSearchListData()
    }

    private fun navSearch(){
        findNavController().navigate(R.id.action_blogFragment_to_searchProductFragment)
    }
}

















