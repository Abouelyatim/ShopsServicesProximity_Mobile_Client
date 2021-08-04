package com.smartcity.client.ui.main.product.products

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.smartcity.client.ui.main.product.BaseProductFragment
import com.smartcity.client.ui.main.product.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.product.state.ProductViewState
import com.smartcity.client.ui.main.product.viewmodel.*
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_blog.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ProductFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseProductFragment(R.layout.fragment_blog,viewModelFactory),
    ProductListAdapter.Interaction,
    ProductGridAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{
    private lateinit var productListRecyclerAdapter: ProductListAdapter
    private lateinit var productGridRecyclerAdapter: ProductGridAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)
        uiCommunicationListener.displayBottomNavigation(true)
        uiCommunicationListener.updateStatusBarColor(R.color.white,false)

        setSelectedRecyclerView(viewModel.getGridOrListView())
        initProductGridRecyclerView()
        initProductListRecyclerView()
        subscribeObservers()

        if(viewModel.getProductList().isEmpty()){
            loadProductMainList()
        }

        setGridListViewBehavior()
        initGridListViewBehavior(viewModel.getGridOrListView())
        searchProduct()
        checkForYou()
    }

    private fun checkForYou() {
        for_you_button.setOnClickListener {
            navForYou()
        }

        for_you_container.setOnClickListener {
            navForYou()
        }
    }

    private fun navForYou(){
        findNavController().navigate(R.id.action_blogFragment_to_forYouFragment)
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
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

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

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            Log.d(TAG, "BlogFragment, ViewState: ${viewState}")
            if(viewState != null){
                productGridRecyclerAdapter.apply {
                    submitList(
                        productList = viewModel.getProductList().distinct(),
                        isQueryExhausted = viewModel.getIsQueryExhausted()
                    )
                }

                productListRecyclerAdapter.apply {
                    submitList(
                        productList = viewModel.getProductList().distinct(),
                        isQueryExhausted = viewModel.getIsQueryExhausted()
                    )
                }
            }
        })
    }

    private  fun resetUI(){
        product_list_recyclerview.smoothScrollToPosition(0)
        product_grid_recyclerview.smoothScrollToPosition(0)
        uiCommunicationListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    private fun initProductGridRecyclerView(){
        product_grid_recyclerview.apply {
            layoutManager = GridLayoutManager(this@ProductFragment.context, 2, GridLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productGridRecyclerAdapter =
                ProductGridAdapter(
                    requestManager,
                    this@ProductFragment
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
            layoutManager = LinearLayoutManager(this@ProductFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productListRecyclerAdapter =
                ProductListAdapter(
                    requestManager,
                    this@ProductFragment
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
        viewModel.clearProductListDataInterest()
    }

    private fun navSearch(){
        findNavController().navigate(R.id.action_blogFragment_to_searchProductFragment)
    }

    private fun initGridListViewBehavior(boolean: Boolean) {
        if(boolean){
            list_view_button.visibility =View.GONE
            grid_view_button.visibility=View.VISIBLE
        }else{
            grid_view_button.visibility=View.GONE
            list_view_button.visibility =View.VISIBLE
        }
    }

    private fun setGridListViewBehavior() {
        list_view_button.setOnClickListener {
            list_view_button.visibility =View.GONE
            grid_view_button.visibility=View.VISIBLE
            viewModel.setGridOrListView(true)
            layoutChangeEvent()
        }

        grid_view_button.setOnClickListener {
            grid_view_button.visibility=View.GONE
            list_view_button.visibility =View.VISIBLE
            viewModel.setGridOrListView(false)
            layoutChangeEvent()
        }
    }

    private fun searchProduct(){
        search_product.setOnClickListener {
            navSearch()
        }
    }

    private fun layoutChangeEvent(){
        resetUI()
        setSelectedRecyclerView(viewModel.getGridOrListView())
    }
}




