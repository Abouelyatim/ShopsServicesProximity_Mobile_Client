package com.smartcity.client.ui.main.product.search

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.main.product.BaseProductFragment
import com.smartcity.client.ui.main.product.products.ProductGridAdapter
import com.smartcity.client.ui.main.product.products.ProductListAdapter
import com.smartcity.client.ui.main.product.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.product.state.ProductStateEvent
import com.smartcity.client.ui.main.product.state.ProductViewState
import com.smartcity.client.ui.main.product.viewmodel.*
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_search_product.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class SearchProductFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseProductFragment(R.layout.fragment_search_product,viewModelFactory),
    ProductListAdapter.Interaction,
    ProductGridAdapter.Interaction
{
    private lateinit var productListRecyclerAdapter: ProductListAdapter
    private lateinit var productGridRecyclerAdapter: ProductGridAdapter

    private lateinit var productSearchView: SearchView

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

        uiCommunicationListener.displayBottomNavigation(false)
        uiCommunicationListener.updateStatusBarColor(R.color.white,false)

        setSelectedRecyclerView(viewModel.getGridOrListViewSearch())
        initProductGridRecyclerView()
        initProductListRecyclerView()
        subscribeObservers()

        setGridListViewBehavior()
        initGridListViewBehavior(viewModel.getGridOrListViewSearch())

        initSearchView()
        onBackClicked()
    }

    private fun onBackClicked() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initSearchView(){
        activity?.apply {
            val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            productSearchView = product_search_view
            productSearchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            productSearchView.maxWidth = Integer.MAX_VALUE
            productSearchView.setIconifiedByDefault(false)
            productSearchView.isSubmitButtonEnabled = true
            productSearchView.requestFocus()
            productSearchView.queryHint="Search"

        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = productSearchView.findViewById(R.id.search_src_text) as EditText

        val backgroundView = productSearchView.findViewById(R.id.search_plate) as View
        backgroundView.background = null

        val backgroundViewSubmit = productSearchView.findViewById(R.id.submit_area) as View
        backgroundViewSubmit.background = null

        searchPlate.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                val searchQuery = v.text.toString()
                viewModel.setQuerySearch(searchQuery)
                searchEvent()
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = productSearchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            viewModel.setQuerySearch(searchQuery)
            searchEvent()
        }
    }

    private fun searchEvent(){
         viewModel.setStateEvent(
             ProductStateEvent.ProductSearchEvent()
         )
    }

    private fun setGridListViewBehavior() {
        list_view_button.setOnClickListener {
            list_view_button.visibility =View.GONE
            grid_view_button.visibility=View.VISIBLE
            viewModel.setGridOrListViewSearch(true)
            layoutChangeEvent()
        }

        grid_view_button.setOnClickListener {
            grid_view_button.visibility=View.GONE
            list_view_button.visibility =View.VISIBLE
            viewModel.setGridOrListViewSearch(false)
            layoutChangeEvent()
        }
    }

    private fun layoutChangeEvent(){
        resetUI()
        setSelectedRecyclerView(viewModel.getGridOrListViewSearch())
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
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.DONE_Back_Clicked_Store_Event)){
                    findNavController().popBackStack()
                }

                uiCommunicationListener.onResponseReceived(
                    response = it.response,
                    stateMessageCallback = object: StateMessageCallback {
                        override fun removeMessageFromStack() {
                            viewModel.clearStateMessage()
                        }
                    }
                )

                if(stateMessage.response.message.equals(SuccessHandling.DONE_Search_Event)){
                    onProductSearch()
                }
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
                        productList = viewModel.getProductListSearch().distinct(),
                        isQueryExhausted = viewModel.getIsQueryExhaustedSearch()
                    )
                }

                productListRecyclerAdapter.apply {
                    submitList(
                        productList = viewModel.getProductListSearch().distinct(),
                        isQueryExhausted = viewModel.getIsQueryExhaustedSearch()
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
        uiCommunicationListener.hideSoftKeyboard()
        focusable_view.requestFocus()
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