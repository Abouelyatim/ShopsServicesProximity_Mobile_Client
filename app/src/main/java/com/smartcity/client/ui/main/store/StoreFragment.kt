package com.smartcity.client.ui.main.store

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.*
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.main.store.ViewCustomCategoryAdapter.Companion.getSelectedPositions
import com.smartcity.client.ui.main.store.ViewCustomCategoryAdapter.Companion.setSelectedPositions
import com.smartcity.client.ui.main.store.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.store.state.StoreStateEvent
import com.smartcity.client.ui.main.store.state.StoreViewState
import com.smartcity.client.util.ActionConstants
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
): BaseStoreFragment(R.layout.fragment_store),
    ViewCustomCategoryAdapter.Interaction,
    ViewCustomCategoryAdapter.InteractionAll,
    ViewProductAdapter.Interaction{

    private lateinit var customCategoryrecyclerAdapter: ViewCustomCategoryAdapter
    private lateinit var productRecyclerAdapter: ViewProductAdapter

    val viewModel: StoreViewModel by viewModels{
        viewModelFactory
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as StoreViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        stateChangeListener.expandAppBar()


        initCustomCategoryRecyclerView()
        initProductRecyclerView()
        subscribeObservers()

        if(viewModel.getCustomCategoryRecyclerPosition()==0){
            CustomCategoryMain()
        }
    }

    private fun initProductRecyclerView() {
        view_product_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@StoreFragment.context,LinearLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productRecyclerAdapter =
                ViewProductAdapter(
                    requestManager,
                    this@StoreFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })

            productRecyclerAdapter.stateRestorationPolicy= StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = productRecyclerAdapter

        }
        //productRecyclerAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    fun CustomCategoryMain(){
        viewModel.setStateEvent(StoreStateEvent.CustomCategoryMain())
    }

    fun initCustomCategoryRecyclerView(){
        view_custom_category_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@StoreFragment.context,LinearLayoutManager.HORIZONTAL, false)

            val rightSpacingDecorator = RightSpacingItemDecoration(15)
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
            customCategoryrecyclerAdapter.stateRestorationPolicy= StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = customCategoryrecyclerAdapter

        }


    }

    private fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)

            if(dataState != null){
                //set Product list get it from network
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        when(response.message){

                            SuccessHandling.DONE_Product_Main->{
                                data.data?.let{
                                    it.peekContent()?.let{
                                        it.viewProductList.let {
                                            viewModel.setViewProductList(it)
                                        }
                                    }

                                }
                            }

                            SuccessHandling.DONE_Custom_Category_Main->{
                                AllProduct()
                                data.data?.let{
                                    it.peekContent()?.let{
                                        viewModel.setViewCustomCategoryFields(it.viewCustomCategoryFields)
                                    }

                                }
                            }

                            SuccessHandling.DONE_All_Product->{
                                data.data?.let{
                                    it.peekContent()?.let{
                                        it.viewProductList.let {
                                            viewModel.setViewProductList(it)
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
            customCategoryrecyclerAdapter.submitList(viewModel.getViewCustomCategoryFields())
            productRecyclerAdapter.apply {
                    submitList(viewModel.getViewProductList().products)
                }


        })
    }

    fun ProductMain(id:Long){
        viewModel.setStateEvent(
            StoreStateEvent.ProductMain(
                id
            ))
    }

    fun AllProduct(){
        viewModel.setStateEvent(
            StoreStateEvent.AllProduct()
        )
    }

    override fun onItemSelected(position: Int, item: CustomCategory) {
        view_custom_category_recyclerview.adapter!!.notifyDataSetChanged()
        ProductMain(item.pk.toLong())
    }

    override fun onItemSelected(item: Product,action:Int) {
        when(action){
            ActionConstants.SELECTED ->{
               viewModel.setViewProductFields(item)
                findNavController().navigate(R.id.action_storeFragment_to_viewProductFragment)
            }
        }

    }



    override fun onItemAddSelected() {
        view_custom_category_recyclerview.adapter!!.notifyDataSetChanged()
        AllProduct()
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