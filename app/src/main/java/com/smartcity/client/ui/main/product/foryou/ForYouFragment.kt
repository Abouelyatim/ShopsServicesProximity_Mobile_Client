package com.smartcity.client.ui.main.product.foryou

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
import com.smartcity.client.ui.main.product.products.ProductGridAdapter
import com.smartcity.client.ui.main.product.state.BLOG_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.product.state.ProductViewState
import com.smartcity.client.ui.main.product.viewmodel.*
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_for_you.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ForYouFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseProductFragment(R.layout.fragment_for_you,viewModelFactory),
    ProductGridAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{

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
        uiCommunicationListener.displayBottomNavigation(false)
        uiCommunicationListener.updateStatusBarColor(R.color.white,false)

        subscribeObservers()
        initProductGridRecyclerView()
        loadProductInterestList()
        backProceed()
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun loadProductInterestList(){
       viewModel.loadProductInterestList()
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

             /*   if(stateMessage.response.message.equals(SuccessHandling.DONE_Product_Layout_Change_Event)){
                    //todo back button
                }*/

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
            if(viewState != null){
                productGridRecyclerAdapter.apply {
                    submitList(
                        productList = viewModel.getProductListInterest().distinct(),
                        isQueryExhausted = viewModel.getIsQueryExhaustedInterest()
                    )
                }
            }
        })
    }

    private fun initProductGridRecyclerView() {
        product_grid_recyclerview.apply {
            layoutManager = GridLayoutManager(this@ForYouFragment.context, 2, GridLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productGridRecyclerAdapter =
                ProductGridAdapter(
                    requestManager,
                    this@ForYouFragment
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
                        viewModel.nextPageInterest()
                    }
                }
            })
            productGridRecyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = productGridRecyclerAdapter

        }
    }

    private  fun resetUI(){
        product_grid_recyclerview.smoothScrollToPosition(0)
        uiCommunicationListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    override fun onGridItemSelected(position: Int, item: Product) {

    }

    override fun restoreGridListPosition() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        product_grid_recyclerview.adapter = null
    }

    fun onProductInterest(){
        viewModel.loadFirstPageInterest().let {
            resetUI()
        }
    }

    override fun onRefresh() {
        onProductInterest()
        swipe_refresh.isRefreshing = false
    }
}