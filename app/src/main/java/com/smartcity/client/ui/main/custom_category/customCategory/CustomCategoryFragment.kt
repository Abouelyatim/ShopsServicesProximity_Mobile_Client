package com.smartcity.client.ui.main.custom_category.customCategory

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager


import com.smartcity.client.R
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.ui.AreYouSureCallback
import com.smartcity.client.ui.UIMessage
import com.smartcity.client.ui.UIMessageType
import com.smartcity.client.ui.main.custom_category.BaseCustomCategoryFragment
import com.smartcity.client.ui.main.custom_category.CustomCategoryViewModel
import com.smartcity.client.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryStateEvent
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.client.util.ActionConstants
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_custom_category.*
import kotlinx.android.synthetic.main.fragment_custom_category.swipe_refresh

import javax.inject.Inject

class CustomCategoryFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCustomCategoryFragment(R.layout.fragment_custom_category),
    CustomCategoryAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{
    private lateinit var recyclerAdapter: CustomCategoryAdapter



    private lateinit var alertDialog: AlertDialog
    val viewModel: CustomCategoryViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as CustomCategoryViewState?)?.let { viewState ->
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

    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)
        stateChangeListener.expandAppBar()

        CustomCategoryMain()
        initvRecyclerView()
        subscribeObservers()
        addCustomCategory()
    }

    private fun addCustomCategory() {
        add_custom_category_button.setOnClickListener {

        }
    }

    fun createCustomCategory(name:String){
        viewModel.setStateEvent(
            CustomCategoryStateEvent.CreateCustomCategory(
                name
            )
        )
    }

    fun updateCustomCategory(id:Long,name:String,provider: Long){
        viewModel.setStateEvent(
            CustomCategoryStateEvent.UpdateCustomCategory(
                id,
                name,
                provider
            )
        )
    }

    fun deleteCustomCategory(id:Long){
        viewModel.setStateEvent(CustomCategoryStateEvent.DeleteCustomCategory(id))
    }



    fun CustomCategoryMain(){
        viewModel.setStateEvent(CustomCategoryStateEvent.CustomCategoryMain())
    }

    fun initvRecyclerView(){
        custom_category_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@CustomCategoryFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter =
                CustomCategoryAdapter(
                    this@CustomCategoryFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = recyclerAdapter
        }

    }

    fun subscribeObservers(){
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            //delete Custom Category success
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(response.message.equals(SuccessHandling.DELETE_DONE)){
                            CustomCategoryMain()
                        }
                    }
                }
            }
            //creation Custom Category success
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(response.message.equals(SuccessHandling.CUSTOM_CATEGORY_CREATION_DONE)){
                            alertDialog.dismiss()
                            CustomCategoryMain()
                        }
                    }
                }
            }
            //update Custom Category success
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->
                        if(response.message.equals(SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE)){
                            alertDialog.dismiss()
                            CustomCategoryMain()
                        }
                    }
                }
            }
            //set Custom Category list get it from network
            dataState.data?.let { data ->
                data.data?.let{
                    it.getContentIfNotHandled()?.let{
                        viewModel.setCustomCategoryFields(it.customCategoryFields)
                    }

                }

            }
        })
        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            recyclerAdapter.submitList(viewModel.getCustomCategoryFields())
        })
    }

    override fun onItemSelected(position: Int, item: CustomCategory, action: Int) {
        when(action){
            ActionConstants.UPDATE ->{

            }

            ActionConstants.DELETE ->{
                val callback: AreYouSureCallback = object: AreYouSureCallback {
                    override fun proceed() {
                        deleteCustomCategory(item.pk.toLong())
                    }
                    override fun cancel() {
                        // ignore
                    }
                }
                uiCommunicationListener.onUIMessageReceived(
                    UIMessage(
                        getString(R.string.are_you_sure_delete),
                        UIMessageType.AreYouSureDialog(callback)
                    )
                )
            }

            ActionConstants.SELECTED -> {
                viewModel.setSelectedCustomCategory(item)
                resetUI()

            }

        }
    }



    override fun onRefresh() {
        CustomCategoryMain()
        swipe_refresh.isRefreshing = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        custom_category_recyclerview.adapter = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.clearProductList()
    }

    private  fun resetUI(){
        custom_category_recyclerview.smoothScrollToPosition(0)
        stateChangeListener.hideSoftKeyboard()
        focusable_view_custom_category.requestFocus()
    }
}

















