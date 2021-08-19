package com.smartcity.client.ui.main.account.orders

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.sumimakito.awesomeqr.AwesomeQrRenderer
import com.github.sumimakito.awesomeqr.RenderResult
import com.github.sumimakito.awesomeqr.option.RenderOption
import com.github.sumimakito.awesomeqr.option.color.Color
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.smartcity.client.R
import com.smartcity.client.models.Order
import com.smartcity.client.models.OrderType
import com.smartcity.client.ui.main.account.BaseAccountFragment
import com.smartcity.client.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.account.viewmodel.getSelectedOrder
import com.smartcity.client.util.Constants
import com.smartcity.client.util.DateUtils.Companion.convertStringToStringDate
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_orders.*
import kotlinx.android.synthetic.main.fragment_view_order.*
import kotlinx.android.synthetic.main.fragment_view_order.back_button
import kotlinx.android.synthetic.main.layout_product_order_item.view.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ViewOrderFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseAccountFragment(R.layout.fragment_view_order,viewModelFactory){

    private  var  orderProductRecyclerAdapter: OrderProductAdapter?=null

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
        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.hideSoftKeyboard()
        uiCommunicationListener.displayBottomNavigation(false)

        viewModel.getSelectedOrder()!!.apply {
            setOrderType(this)
            setStoreDetail(this)
            setOrderDetail(this)
            setReceiverDetail(this)
            setContentOrder(this)
            setBillOrder(this)
        }
        setQrCode()
        backProceed()
    }

    private fun backProceed() {
        back_button.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setQrCode() {
        generate_qr_button.setOnClickListener {
            showQrCodeDialog()
        }
    }

    private fun showQrCodeDialog() {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        dialogView = layoutInflater.inflate(R.layout.dialog_qr_code, null)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)

        val qrImageView = dialogView.findViewById<ImageView>(R.id.qr_code_image)

        generateQrCode()?.let {
            requestManager
                .asBitmap()
                .load(it)
                .into(qrImageView)
        }

        dialog.show()
    }

    private fun generateQrCode():Bitmap?{
        val renderOption = RenderOption()
        renderOption.content = viewModel.getSelectedOrder()!!.id.toString() // content to encode
        renderOption.size = 800 // size of the final QR code image
        renderOption.borderWidth = 0 // width of the empty space around the QR code
        renderOption.patternScale = 0.35f // (optional) specify a scale for patterns
        renderOption.roundedPatterns = true // (optional) if true, blocks will be drawn as dots instead
        renderOption.clearBorder = true // if set to true, the background will NOT be drawn on the border area
        val color = Color()
        color.light = 0xFFFFFFFF.toInt() // for blank spaces
        color.dark = ResourcesCompat.getColor(resources, R.color.bleu,null) // for non-blank spaces
        color.background = ResourcesCompat.getColor(resources, R.color.bleu_light,null)
        color.auto = false
        renderOption.color = color
        
        try {
            val result = AwesomeQrRenderer.render(renderOption)
            if (result.bitmap != null) {
                return result.bitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Oops, something gone wrong.
        }
        return null
    }

    private fun setOrderType(order:Order) {
        when(order.orderType){
            OrderType.DELIVERY ->{
                order_type.text="Delivery"
                order_type_delivery.visibility=View.VISIBLE
                order_store_address_container.visibility=View.GONE
            }

            OrderType.SELFPICKUP ->{
                order_type.text="Self pickup"
                order_type_self_pickup.visibility=View.VISIBLE
                order_delivery_address_container.visibility=View.GONE
            }
        }
    }

    private fun setStoreDetail(order:Order) {
        order_store_name.text=order.storeName
        order_store_address.text=order.storeAddress
    }

    @SuppressLint("SetTextI18n")
    private fun setOrderDetail(order:Order) {
        order_id.text=order.id.toString()
        order_date.text=convertStringToStringDate(order.createAt!!)
        order.address?.let {item->
            order_delivery_address.text=item.fullAddress
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setReceiverDetail(order:Order){
        order_receiver.text="${order.firstName} ${order.lastName} born in ${order.birthDay}"
    }

    private fun setContentOrder(order:Order){
        initProductsRecyclerView()
        orderProductRecyclerAdapter!!.submitList(order.orderProductVariants)
    }

    @SuppressLint("SetTextI18n")
    private fun setBillOrder(order:Order){
        order_total.text=order.bill!!.total.toString()+ Constants.DOLLAR
        order_paid.text=order.bill!!.alreadyPaid.toString()+ Constants.DOLLAR
        order_rest.text=(order.bill!!.total-order.bill!!.alreadyPaid).toString()+ Constants.DOLLAR
    }

    fun initProductsRecyclerView(){
        products_order_recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            orderProductRecyclerAdapter =
                OrderProductAdapter(
                    requestManager
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = orderProductRecyclerAdapter
        }
    }
}