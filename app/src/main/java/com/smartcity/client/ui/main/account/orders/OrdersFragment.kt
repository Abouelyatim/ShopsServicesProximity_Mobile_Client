package com.smartcity.client.ui.main.account.orders

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smartcity.client.R
import com.smartcity.client.models.Order
import com.smartcity.client.ui.AreYouSureCallback
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.orders.OrderActionAdapter.Companion.getSelectedActionPositions
import com.smartcity.client.ui.main.account.orders.OrderActionAdapter.Companion.setSelectedActionPositions
import com.smartcity.client.ui.main.account.orders.OrdersFragment.ActionOrder.FINALIZED
import com.smartcity.client.ui.main.account.orders.OrdersFragment.ActionOrder.IN_PROGRESS
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.*
import com.smartcity.client.util.*
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class OrdersFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_orders,viewModelFactory),
    OrdersAdapter.Interaction,
    OrderActionAdapter.Interaction,
    OrderFilterAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener{

    object ActionOrder {
        val IN_PROGRESS = Pair<String,Int>("In progress",0)
        val FINALIZED = Pair<String,Int>("Finalized",1)
    }

    private lateinit var recyclerOrderActionAdapter: OrderActionAdapter
    private lateinit var recyclerOrdersAdapter: OrdersAdapter

    private var sortRecyclerOrdersAdapter: OrderFilterAdapter? = null
    private var typeRecyclerOrdersAdapter: OrderFilterAdapter? = null
    private var statusRecyclerOrdersAdapter: OrderFilterAdapter? = null
    private lateinit var dialogView: View

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            ACCOUNT_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        swipe_refresh.setOnRefreshListener(this)
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.hideSoftKeyboard()
        uiCommunicationListener.displayBottomNavigation(false)

        initOrderActionRecyclerView()
        initRecyclerView()
        subscribeObservers()

        setOrderAction()
        initData(viewModel.getOrderActionRecyclerPosition())
        setEmptyListUi(viewModel.getOrdersList().isEmpty())
        setOrderFilter()

        backProceed()
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun initData(actionPosition: Int) {
        when(actionPosition){
            IN_PROGRESS.second ->{
                viewModel.setSelectedSortFilter(null)
                viewModel.setSelectedTypeFilter(null)
                viewModel.setSelectedStatusFilter(null)
                getInProgressOrders(
                    "DESC",
                    "",
                    ""
                )
            }
            FINALIZED.second ->{
                viewModel.setSelectedSortFilter(null)
                viewModel.setSelectedTypeFilter(null)
                viewModel.setSelectedStatusFilter(null)
                getFinalizedOrders(
                    "DESC",
                    "",
                    "",
                    ""
                )
            }
        }
    }

    private fun getInProgressOrders(date:String,amount:String,type:String){
        viewModel.setStateEvent(
            AccountStateEvent.GetUserInProgressOrdersEvent(
                date,
                amount,
                type
            )
        )
    }

    private fun getFinalizedOrders(date:String,amount:String,type:String,status:String){
        viewModel.setStateEvent(
            AccountStateEvent.GetUserFinalizedOrdersEvent(
                date,
                amount,
                type,
                status
            )
        )
    }

    private fun initOrderActionRecyclerView() {
        order_action_recyclerview.apply {
            layoutManager = GridLayoutManager(this@OrdersFragment.context, 2, GridLayoutManager.VERTICAL, false)

            val rightSpacingDecorator = RightSpacingItemDecoration(0)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            recyclerOrderActionAdapter =
                OrderActionAdapter(
                    this@OrdersFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            recyclerOrderActionAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerOrderActionAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE)){
                    initData(viewModel.getOrderActionRecyclerPosition())
                }

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

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            setEmptyListUi(viewModel.getOrdersList().isEmpty())

            recyclerOrdersAdapter.submitList(
                viewModel.getOrdersList()
            )

            recyclerOrderActionAdapter.apply {
                submitList(
                    viewModel.getOrderAction()
                )
            }

            sortRecyclerOrdersAdapter?.submitList(
                sortFilter.map { it.first },
                if (viewModel.getSelectedSortFilter() == null) "" else viewModel.getSelectedSortFilter()!!.first
            )

            typeRecyclerOrdersAdapter?.submitList(
                typeFilter.map { it.first },
                if (viewModel.getSelectedTypeFilter() == null) "" else viewModel.getSelectedTypeFilter()!!.first
            )

            statusRecyclerOrdersAdapter?.submitList(
                statusFilter.map { it.first },
                if (viewModel.getSelectedStatusFilter() == null) "" else viewModel.getSelectedStatusFilter()!!.first
            )
        })
    }

    private fun setOrderAction(){
        val list= mutableListOf<Triple<String,Int,Int>>()
        list.add(IN_PROGRESS.second,Triple(IN_PROGRESS.first, R.drawable.ic_baseline_settings_24_white, R.drawable.ic_baseline_settings_24_black))
        list.add(FINALIZED.second,Triple(FINALIZED.first,R.drawable.ic_outline_pending_24_white, R.drawable.ic_outline_pending_24_black))
        viewModel.setOrderActionList(
            list
        )
    }

    fun initRecyclerView(){
        orders_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@OrdersFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerOrdersAdapter =
                OrdersAdapter(
                    requestManager,
                    this@OrdersFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = recyclerOrdersAdapter
        }

    }

    override fun selectedOrder(item: Order) {
        viewModel.setSelectedOrder(item)
        navViewOrder()
    }


    override fun confirmReceived(order: Order) {
        val callback: AreYouSureCallback = object: AreYouSureCallback {
            override fun proceed() {
                viewModel.setStateEvent(
                    AccountStateEvent.ConfirmOrderReceivedEvent(
                        order.id!!
                    )
                )
            }

            override fun cancel() {
                // ignore
            }
        }
        uiCommunicationListener.onResponseReceived(
            response = Response(
                message = getString(R.string.are_you_sure_received_order),
                uiComponentType = UIComponentType.AreYouSureDialog(callback),
                messageType = MessageType.Info()
            ),
            stateMessageCallback = object: StateMessageCallback{
                override fun removeMessageFromStack() {
                    viewModel.clearStateMessage()
                }
            }
        )
    }


    private fun navViewOrder() {
        findNavController().navigate(R.id.action_ordersFragment_to_viewOrderFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setOrderActionRecyclerPosition(getSelectedActionPositions())
        recyclerOrderActionAdapter.resetSelectedActionPosition()
        //orders_recyclerview.adapter=null
        //order_action_recyclerview.adapter=null
    }

    override fun onResume() {
        super.onResume()
        setSelectedActionPositions(viewModel.getOrderActionRecyclerPosition())
    }

    private  fun resetUI(){
        orders_recyclerview.smoothScrollToPosition(0)
        uiCommunicationListener.hideSoftKeyboard()
        focusable_view.requestFocus()
    }

    override fun onActionItemSelected(position: Int, item: String) {
        order_action_recyclerview.adapter!!.notifyDataSetChanged()
        resetUI()
        viewModel.setOrderActionRecyclerPosition(position)
        viewModel.clearOrderList()

        when(item){
            IN_PROGRESS.first ->{
                viewModel.setSelectedSortFilter(null)
                viewModel.setSelectedTypeFilter(null)
                viewModel.setSelectedStatusFilter(null)
                getInProgressOrders(
                    "DESC",
                    "",
                    ""
                )
            }
            FINALIZED.first ->{
                viewModel.setSelectedSortFilter(null)
                viewModel.setSelectedTypeFilter(null)
                viewModel.setSelectedStatusFilter(null)
                getFinalizedOrders(
                    "DESC",
                    "",
                    "",
                    ""
                )
            }
        }
    }

    private fun setEmptyListUi(empty:Boolean){
        if(empty){
            empty_list.visibility=View.VISIBLE
        }else{
            empty_list.visibility=View.GONE
        }
    }

    private fun initFilterOrderRecyclerView(recyclerView: RecyclerView,recyclerAdapter:OrderFilterAdapter) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrdersFragment.context,LinearLayoutManager.HORIZONTAL,false)

            val rightSpacingDecorator = RightSpacingItemDecoration(0)
            removeItemDecoration(rightSpacingDecorator) // does nothing if not applied already
            addItemDecoration(rightSpacingDecorator)

            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()

                }
            })
            recyclerOrderActionAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
            adapter = recyclerAdapter
        }
    }

    private fun setOrderFilter() {
        filter_button.setOnClickListener {
            showFilterDialog()
        }
    }

    private val sortFilter= listOf(
        Triple("Date : Oldest first","date","ASC"),
        Triple("Date : Recent first","date","DESC"),
        Triple("Total : Lowest first","amount","ASC"),
        Triple("Total : Highest first","amount","DESC"),
    )

    private val typeFilter = listOf(
        Triple("Pick up","type","SELFPICKUP"),
        Triple("delivery","type","DELIVERY")
    )

    private val statusFilter = listOf(
        Triple("Accepted","status","ACCEPTED"),
        Triple("Rejected","status","REJECTED")
    )

    private fun showFilterDialog(){
        val dialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
        dialogView = layoutInflater.inflate(R.layout.dialog_filter_order, null)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)

        val position = getSelectedActionPositions()

        val sortRecyclerView = dialogView.findViewById<RecyclerView>(R.id.filter_sort_orders)
        sortRecyclerOrdersAdapter = OrderFilterAdapter(this@OrdersFragment)
        initFilterOrderRecyclerView(sortRecyclerView,sortRecyclerOrdersAdapter!!)
        sortRecyclerOrdersAdapter!!.submitList(
            sortFilter.map { it.first },
            if (viewModel.getSelectedSortFilter() == null) "" else viewModel.getSelectedSortFilter()!!.first
        )

        val typeRecyclerView = dialogView.findViewById<RecyclerView>(R.id.filter_type_orders)
        typeRecyclerOrdersAdapter = OrderFilterAdapter(this@OrdersFragment)
        initFilterOrderRecyclerView(typeRecyclerView,typeRecyclerOrdersAdapter!!)
        typeRecyclerOrdersAdapter!!.submitList(
            typeFilter.map { it.first },
            if (viewModel.getSelectedTypeFilter() == null) "" else viewModel.getSelectedTypeFilter()!!.first
        )

        val statusRecyclerView = dialogView.findViewById<RecyclerView>(R.id.filter_status_orders)
        statusRecyclerOrdersAdapter = OrderFilterAdapter(this@OrdersFragment)
        initFilterOrderRecyclerView(statusRecyclerView,statusRecyclerOrdersAdapter!!)
        statusRecyclerOrdersAdapter!!.submitList(
            statusFilter.map { it.first },
            if (viewModel.getSelectedStatusFilter() == null) "" else viewModel.getSelectedStatusFilter()!!.first
        )

        val statusFilterContainer = dialogView.findViewById<LinearLayout>(R.id.status_filter_container)
        when(position) {
            IN_PROGRESS.second -> {
                statusFilterContainer.visibility = View.GONE
            }

            FINALIZED.second ->{
                statusFilterContainer.visibility = View.VISIBLE
            }
        }

        val viewOrdersButton = dialogView.findViewById<Button>(R.id.view_orders_button)
        viewOrdersButton.setOnClickListener {

            when(position){
                IN_PROGRESS.second ->{
                    getInProgressOrders(
                        if (viewModel.getSelectedSortFilter() == null) "DESC" else (if(viewModel.getSelectedSortFilter()!!.second == "date") viewModel.getSelectedSortFilter()!!.third else ""),
                        if (viewModel.getSelectedSortFilter() == null) "" else (if(viewModel.getSelectedSortFilter()!!.second == "amount") viewModel.getSelectedSortFilter()!!.third else ""),
                        if (viewModel.getSelectedTypeFilter() == null) "" else viewModel.getSelectedTypeFilter()!!.third
                    )
                }
                FINALIZED.second ->{
                    getFinalizedOrders(
                        if (viewModel.getSelectedSortFilter() == null) "DESC" else (if(viewModel.getSelectedSortFilter()!!.second == "date") viewModel.getSelectedSortFilter()!!.third else ""),
                        if (viewModel.getSelectedSortFilter() == null) "" else (if(viewModel.getSelectedSortFilter()!!.second == "amount") viewModel.getSelectedSortFilter()!!.third else ""),
                        if (viewModel.getSelectedTypeFilter() == null) "" else viewModel.getSelectedTypeFilter()!!.third,
                        if (viewModel.getSelectedStatusFilter() == null) "" else viewModel.getSelectedStatusFilter()!!.third
                    )
                }
            }
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onRefresh() {
        initData(viewModel.getOrderActionRecyclerPosition())
        swipe_refresh.isRefreshing = false
    }

    override fun onItemSelected(item: String) {
        if(item in sortFilter.map { it.first }){
            viewModel.setSelectedSortFilter(sortFilter.find { it.first == item }!!)
            sortRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
        if(item in typeFilter.map { it.first }){
            viewModel.setSelectedTypeFilter(typeFilter.find { it.first == item }!!)
            typeRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
        if(item in statusFilter.map { it.first }){
            viewModel.setSelectedStatusFilter(statusFilter.find { it.first == item }!!)
            statusRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onItemDeSelected(item: String) {
        if(item in sortFilter.map { it.first }){
            viewModel.setSelectedSortFilter(null)
            sortRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
        if(item in typeFilter.map { it.first }){
            viewModel.setSelectedTypeFilter(null)
            typeRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
        if(item in statusFilter.map { it.first }){
            viewModel.setSelectedStatusFilter(null)
            statusRecyclerOrdersAdapter!!.notifyDataSetChanged()
        }
    }
}