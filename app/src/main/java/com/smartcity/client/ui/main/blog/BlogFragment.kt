package com.smartcity.client.ui.main.blog

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.main.blog.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.viewmodel.*
import com.smartcity.client.util.TopSpacingItemDecoration
import handleIncomingBlogListData
import kotlinx.android.synthetic.main.fragment_blog.*
import loadFirstPage
import loadFirstSearchPage
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
    SwipeRefreshLayout.OnRefreshListener
{

    val viewModel: ProductViewModel by viewModels{
        viewModelFactory
    }

    private lateinit var searchView: SearchView
    private lateinit var recyclerAdapter: ProductListAdapter

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

        initRecyclerView()
        subscribeObservers()

        loadProductMainList()

    }

    fun loadProductMainList(){
        viewModel.loadProductMainList()
    }


    private fun saveLayoutManagerState(){
        product_recyclerview.layoutManager?.onSaveInstanceState()?.let { lmState ->
            viewModel.setLayoutManagerState(lmState)
        }
    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)

            if(dataState != null) {
                //handlePagination(dataState)
                handlePagination(dataState)
            }
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer{ viewState ->
            Log.d(TAG, "BlogFragment, ViewState: ${viewState}")
            if(viewState != null){
                recyclerAdapter.apply {
                    submitList(
                        productList = viewState.productFields.productList.distinct(),
                        isQueryExhausted = viewState.productFields.isQueryExhausted
                    )
                }

            }
        })
    }


    private  fun resetUI(){
        product_recyclerview.smoothScrollToPosition(0)
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

    private fun initRecyclerView(){

        product_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@BlogFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = ProductListAdapter(
                requestManager,
                this@BlogFragment
            )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == recyclerAdapter.itemCount.minus(1)) {
                        Log.d(TAG, "BlogFragment: attempting to load next page...")
                        viewModel.nextPage()
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_menu, menu)
        initSearchView(menu)
    }

    private fun initSearchView(menu: Menu){
        activity?.apply {
            val searchManager: SearchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = searchView.findViewById(R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: ${searchQuery}")
                viewModel.setQuery(searchQuery).let{
                    onProductSearch()
                }
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = searchView.findViewById(R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: ${searchQuery}")
            viewModel.setQuery(searchQuery).let {
                onProductSearch()
            }

        }

        val closeButton = searchView.findViewById(R.id.search_close_btn) as View
        closeButton.setOnClickListener {
            searchView.isIconified=true
            onProductMain()
        }
    }


    override fun onItemSelected(position: Int, item: Product) {
        viewModel.setViewProductFields(item)
        findNavController().navigate(R.id.action_blogFragment_to_viewProductFragment)
    }

    override fun restoreListPosition() {
        viewModel.viewState.value?.productFields?.layoutManagerState?.let { lmState ->
            product_recyclerview?.layoutManager?.onRestoreInstanceState(lmState)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        product_recyclerview.adapter = null
       // viewModel.clearProductListData()
    }

    fun onProductSearch(){
        viewModel.loadFirstSearchPage().let {
            resetUI()
        }
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
        saveLayoutManagerState()
    }


}

















