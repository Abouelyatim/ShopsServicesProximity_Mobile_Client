package com.smartcity.client.ui.main.flash_notification

import android.R.attr.path
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.here.oksse.OkSse
import com.here.oksse.ServerSentEvent
import com.smartcity.client.R
import com.smartcity.client.ui.main.flash_notification.state.CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.flash_notification.state.FlashStateEvent
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.ui.main.flash_notification.viewmodel.FlashViewModel
import com.smartcity.client.ui.main.flash_notification.viewmodel.getFlashDealsList
import com.smartcity.client.ui.main.flash_notification.viewmodel.setFlashDealsList
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_flash_notification.*
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


class FlashFlashNotificationFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseFlashNotificationFragment(R.layout.fragment_flash_notification)
{

    private lateinit var flashRecyclerAdapter: FlashDealsAdapter

    val viewModel: FlashViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY] as FlashViewState?)?.let { viewState ->
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
            CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
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
        stateChangeListener.displayBadgeBottomNavigationFlash(false)

      /*  val listener= object:ServerSentEvent.Listener{
            override fun onOpen(sse: ServerSentEvent?, response: Response?) {

            }

            override fun onRetryTime(sse: ServerSentEvent?, milliseconds: Long): Boolean {
                return true
            }

            override fun onComment(sse: ServerSentEvent?, comment: String?) {

            }

            override fun onRetryError(
                sse: ServerSentEvent?,
                throwable: Throwable?,
                response: Response?
            ): Boolean {
                return true
            }

            override fun onPreRetry(sse: ServerSentEvent?, originalRequest: Request?): Request {
                return originalRequest!!
            }

            override fun onMessage(
                sse: ServerSentEvent?,
                id: String?,
                event: String?,
                message: String?
            ) {
                Log.d("ii",message)
            }

            override fun onClosed(sse: ServerSentEvent?) {

            }

        }

        val request: Request = Request.Builder().url("http://192.168.42.196:8085/sse/flux/order-change-event").build()
        val okSse = OkSse()
         sse = okSse.newServerSentEvent(request, listener)*/

        getFlashDeals()
        initRecyclerView()
        subscribeObservers()

    }
    //lateinit var sse:ServerSentEvent

    private fun getFlashDeals() {
        viewModel.setStateEvent(
            FlashStateEvent.GetUserFlashDealsEvent()
        )
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            //set Offer list get it from network
            dataState.data?.let { data ->
                data.data?.let{
                    it.getContentIfNotHandled()?.let{
                        it.flashFields.flashDealsList.let {
                            viewModel.setFlashDealsList(it)
                        }
                    }

                }

            }
        })

        //submit list to recycler view
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            flashRecyclerAdapter.submitList(viewModel.getFlashDealsList())
        })
    }

    private fun initRecyclerView() {
        flash_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@FlashFlashNotificationFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            flashRecyclerAdapter =
                FlashDealsAdapter(

                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = flashRecyclerAdapter
        }
    }


    override fun onPause() {
        super.onPause()
       // Log.d("ii","sse.close()")
       // sse.close()
    }
}