package com.smartcity.client.ui.main.cart.address

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.smartcity.client.R
import com.smartcity.client.models.Address
import com.smartcity.client.ui.main.cart.BaseCartFragment
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.ui.main.cart.viewmodel.getAddressList
import com.smartcity.client.ui.main.cart.viewmodel.setDeliveryAddress
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_pick_address.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class PickAddressFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCartFragment(R.layout.fragment_pick_address,viewModelFactory),
    PickAddressAdapter.Interaction
{

    private lateinit var recyclerPickAddressAdapter: PickAddressAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as CartViewState?)?.let { viewState ->
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.hideSoftKeyboard()
        uiCommunicationListener.displayBottomNavigation(false)

        initRecyclerView()
        add_address_button.setOnClickListener {
            navAddAddress()
        }

    }

    fun initRecyclerView(){
        pick_address_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@PickAddressFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerPickAddressAdapter =
                PickAddressAdapter(
                    this@PickAddressFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = recyclerPickAddressAdapter
        }
        viewModel.getAddressList().apply {
            recyclerPickAddressAdapter.submitList(
                this
            )
            setEmptyListUi(this.isEmpty())
        }
    }

    override fun selectedAddress(address: Address) {
        viewModel.setDeliveryAddress(address)
        navPlaceOrder()
    }

    private fun navPlaceOrder(){
        findNavController().popBackStack()
    }

    private fun navAddAddress(){
        findNavController().navigate(R.id.action_pickAddressFragment_to_addAddressFragment)
    }

    private fun setEmptyListUi(empty:Boolean){
        if(empty){
            empty_list.visibility=View.VISIBLE
        }else{
            empty_list.visibility=View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //pick_address_recyclerview.adapter=null
    }
}