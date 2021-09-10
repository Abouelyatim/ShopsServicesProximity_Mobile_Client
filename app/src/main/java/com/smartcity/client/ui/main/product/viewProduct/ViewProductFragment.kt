package com.smartcity.client.ui.main.product.viewProduct

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.smartcity.client.R
import com.smartcity.client.models.product.AttributeValue
import com.smartcity.client.models.product.OfferType
import com.smartcity.client.models.product.Product
import com.smartcity.client.models.product.ProductVariants
import com.smartcity.client.ui.main.cart.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.product.BaseProductFragment
import com.smartcity.client.ui.main.product.state.ProductStateEvent
import com.smartcity.client.ui.main.product.state.ProductViewState
import com.smartcity.client.ui.main.product.viewProduct.adapters.OptionsAdapter
import com.smartcity.client.ui.main.product.viewProduct.adapters.ValuesAdapter
import com.smartcity.client.ui.main.product.viewProduct.adapters.VariantImageAdapter
import com.smartcity.client.ui.main.product.viewProduct.adapters.ViewPagerAdapter
import com.smartcity.client.ui.main.product.viewmodel.clearChoisesMap
import com.smartcity.client.ui.main.product.viewmodel.getChoisesMap
import com.smartcity.client.ui.main.product.viewmodel.getViewProductFields
import com.smartcity.client.ui.main.product.viewmodel.setChoisesMap
import com.smartcity.client.util.Constants
import com.smartcity.client.util.StateMessageCallback
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_view_product.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@FlowPreview
@ExperimentalCoroutinesApi
class ViewProductFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseProductFragment(R.layout.fragment_view_product,viewModelFactory),
    OptionsAdapter.Interaction,
    VariantImageAdapter.Interaction,
    OnMapReadyCallback
{
    private lateinit var mMapView:MapView
    private lateinit var dialogView: View
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var product: Product
    private lateinit var  variantImageRecyclerAdapter: VariantImageAdapter
    private lateinit var  optionsRecyclerAdapter: OptionsAdapter
    private lateinit var optionsRecyclerview: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.cancelActiveJobs()
        // Restore state after process death
        savedInstanceState?.let { inState ->
            (inState[CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY] as ProductViewState?)?.let { viewState ->
                viewModel.setViewState(viewState)
            }
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY,
            viewModel.viewState.value
        )
        super.onSaveInstanceState(outState)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        uiCommunicationListener.expandAppBar()
        uiCommunicationListener.displayBottomNavigation(false)
        uiCommunicationListener.updateStatusBarColor(R.color.white,false)

        viewModel.getViewProductFields()?.let {
            product=it
        }

        setStoreAddressMap(savedInstanceState)
        initViewPager()
        setNewPrice(product_new_price)
        setDiscountValue()
        setName()
        setOptions()
        setDescription()
        variantsDialog()
        setStoreAddress()
        SetOpenGoogleMap()
        SetStoreName()
        setNavViewStore()
        subscribeObservers()
        onBackClicked()
        saveProductClicked()
    }

    private fun saveProductClicked(){
        viewModel.setStateEvent(
            ProductStateEvent.SaveClickedProductEvent(
                product.id
            )
        )
    }

    private fun onBackClicked() {
        back_button.setOnClickListener {
           findNavController().popBackStack()
        }
    }

    private fun setNavViewStore() {
        product_view_store_button.setOnClickListener { navStore() }
        product_store_container.setOnClickListener { navStore() }
    }

    private fun navStore(){
        findNavController().navigate(R.id.action_viewProductFragment_to_storeFragment)
    }

    private fun SetStoreName() {
        product_store_name.text=product.storeName
    }

    private fun SetOpenGoogleMap() {
        product_store_address_open_google_map.setOnClickListener {
            product.storeAddress?.let {
                if (it.latitude != 0.0 && it.longitude != 0.0) {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=${it.latitude},${it.longitude}(Google)")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    startActivity(mapIntent)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setStoreAddressMap(savedInstanceState: Bundle?) {
        val mainScrollView: NestedScrollView = requireActivity().findViewById(R.id.scrollView) as NestedScrollView
        val transparentImageView = requireActivity().findViewById(R.id.transparent_image) as ImageView
        transparentImageView.setOnTouchListener { v, event ->
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    // Disallow ScrollView to intercept touch events.
                    mainScrollView.requestDisallowInterceptTouchEvent(true)
                    // Disable touch on transparent view
                    false
                }
                MotionEvent.ACTION_UP -> {
                    // Allow ScrollView to intercept touch events.
                    mainScrollView.requestDisallowInterceptTouchEvent(false)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    mainScrollView.requestDisallowInterceptTouchEvent(true)
                    false
                }
                else -> true
            }
        }
        mMapView = requireActivity().findViewById(R.id.product_store_address_map) as MapView
        mMapView.onCreate(savedInstanceState)
        mMapView.onResume() // needed to get the map to display immediately
        mMapView.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap!!.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(context,R.raw.store_view_map_style)
        )

        product.storeAddress?.let {
            if(it.latitude!=0.0 && it.longitude!=0.0){
                val marker=googleMap!!.addMarker(
                    MarkerOptions()
                        .draggable(false)
                        .position(LatLng(it.latitude, it.longitude))
                        .title(product.storeName)
                )
                marker.showInfoWindow()
                val zoomLevel = 14.0f
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), zoomLevel))
                googleMap.setMinZoomPreference(16.0f)
            }
        }
    }

    private fun setStoreAddress() {
        product.storeAddress?.let {
            product_store_address.text= it.fullAddress
        }
    }

    private fun initViewPager() {
        viewPager = requireActivity().findViewById(R.id.view_pager)
        viewPagerAdapter =
            ViewPagerAdapter(
                requestManager
            )
        val images=product.images.map { it.image }
        viewPagerAdapter.setImageUrls(images)
        viewPager.adapter = viewPagerAdapter
        if (images.size<2){
            dotsIndicator.visibility=View.GONE
        }
        dotsIndicator.setViewPager(view_pager)
        view_pager.adapter?.registerDataSetObserver(dotsIndicator.dataSetObserver)
    }

    @SuppressLint("SetTextI18n")
    private fun setPrice(view: View) {
        (view as TextView).text=getPrice()
    }

    @SuppressLint("SetTextI18n")
    private fun getPrice():String {
        val prices = mutableListOf<Double>()
        product.productVariants.map {
            val offer=it.offer
            if (offer!=null){
                when(offer.type){
                    OfferType.PERCENTAGE ->{
                        prices.add(BigDecimal(it.price-(it.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                    }

                    OfferType.FIXED ->{
                        prices.add(it.price-offer.newPrice!!)
                    }
                    null -> {}
                }
            }else{
                prices.add(it.price)
            }
        }

        if(prices.max() != prices.min()){
            return "${Constants.DOLLAR} ${prices.min()} - ${prices.max()}"
        }else{
            return "${prices.max()}${Constants.DOLLAR}"
        }
    }

    private fun setName() {
        product_name.text=product.name
    }

    @SuppressLint("SetTextI18n")
    private fun setNewPrice(view: View) {
        val prices = mutableListOf<Double>()
        product.productVariants.map {
            val offer=it.offer
            if (offer!=null){
                when(offer.type){
                    OfferType.PERCENTAGE ->{
                        prices.add(BigDecimal(it.price-(it.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble())
                    }

                    OfferType.FIXED ->{
                        prices.add(it.price-offer.newPrice!!)
                    }
                    null -> {}
                }
            }else{
                prices.add(it.price)
            }
        }


        if(prices.max() != prices.min()){
            (view as TextView).text= "${Constants.DOLLAR} ${prices.min()} - ${prices.max()}"
        }else{
            (view as TextView).text= "${prices.max()}${Constants.DOLLAR}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setOldPrice(){
        product_old_price.visibility=View.VISIBLE
        val prices =product.productVariants.map { productVariant -> productVariant.price }

        if(prices.max() != prices.min()){
            product_old_price.text= "${Constants.DOLLAR} ${prices.min()} - ${prices.max()}"
        }else{
            product_old_price.text= "${prices.max()}${Constants.DOLLAR}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDiscountValue(){
        val percentages= mutableListOf<Int>()
        val fixed = mutableListOf<Double>()
        product.productVariants.map {
            val offer=it.offer
            if(offer!=null){
                when(offer.type){
                    OfferType.PERCENTAGE ->{
                        percentages.add(offer.percentage!!)
                    }

                    OfferType.FIXED ->{
                        fixed.add(offer.newPrice!!)
                    }
                }
            }
        }

        if(percentages.isNotEmpty()){
            discount_percentage.visibility=View.VISIBLE
            discount_percentage.text="-${percentages.max()}%"
            setOldPrice()
        }


        if(fixed.isNotEmpty()){
            discount_fixed.visibility=View.VISIBLE
            discount_fixed.text="-${fixed.max()} ${Constants.DOLLAR}"
            setOldPrice()
        }
    }

    private fun setOptions() {
        if (product.attributes.isNotEmpty()){//product with variantes
            var options=""
            product.attributes.map {
                options=options+" , "+it.attributeValues.size.toString()+" "+it.name
            }
            product_attrebute.text=options.drop(2)
            initVariantImageRecyclerView()

            val imagesList=product.productVariants.map {
                it.image
            }

            if (imagesList.contains(null)){//varinates witout images
                product_recyclerview_variant_image.visibility=View.GONE
            }else{//varinates with images
                variantImageRecyclerAdapter.submitList(
                    product.productVariants.map {
                        it.image!!
                    }.distinct()
                )
            }

        }else{
            product_attrebute.text="Stock"
        }

    }

    private fun setDescription() {
        product_description.text=product.description
        expand_description.setOnClickListener {
            if (product_description.visibility==View.GONE){
                TransitionManager.beginDelayedTransition(card_view, AutoTransition())
                product_description.visibility=View.VISIBLE
                expand_description.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_up_24)
            }else{
                TransitionManager.beginDelayedTransition(card_view, AutoTransition())
                product_description.visibility=View.GONE
                expand_description.setBackgroundResource(R.drawable.ic_baseline_keyboard_arrow_down_24)
            }
        }
    }

    private fun variantsDialog() {
        options_view.setOnClickListener {
            showVariantDialog()
        }
    }

    private fun subscribeObservers() {
        viewModel.stateMessage.observe(viewLifecycleOwner, Observer { stateMessage ->//must

            stateMessage?.let {

                if(stateMessage.response.message.equals(SuccessHandling.DONE_Back_Clicked_View_product_Event)){
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
            }
        })

        viewModel.numActiveJobs.observe(viewLifecycleOwner, Observer { jobCounter ->//must
            uiCommunicationListener.displayProgressBar(viewModel.areAnyJobsActive())
        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            val map=viewModel.getChoisesMap()
            val sortedOption= mutableListOf<String>()
            product.attributes.map {
                sortedOption.add(it.name)
            }

            val resultSortedMap= sortMapByList(map,sortedOption)

            //show only valid option value
            ValuesAdapter.setAvailableOptionValue(getExistedOptionValue(resultSortedMap))

            if (map.isNotEmpty()){
                if(map.size==product.attributes.size){
                    showDetailedProduct(map)
                }else{
                    showDefaultProduct()
                }
            }
        })
    }

    private fun showVariantDialog(){
        val dialog = Dialog(requireContext(), android.R.style.Theme_Light)
        dialog.window.setBackgroundDrawable( ColorDrawable(Color.parseColor("#99000000")))
        dialogView = layoutInflater.inflate(R.layout.dialog_variants, null)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(dialogView)

        val rec=dialogView.findViewById<RecyclerView>(R.id.options_recyclerview_variant)
        optionsRecyclerview=rec
        initOptionsRecyclerView(rec)

        product.attributes.map {//init adapter positions
            ValuesAdapter.getMapSelectedPosition().put(it.name,-1)
            ValuesAdapter.getOldPosition().put(it.name,-1)
        }

        optionsRecyclerAdapter.submitList(
            product.attributes
        )

        val cancel=dialogView.findViewById<View>(R.id.cancel)
        cancel!!.setOnClickListener {
            dialog.dismiss()
        }
        val layoutCancel=dialogView.findViewById<View>(R.id.cancel_view)
        layoutCancel!!.setOnClickListener {
            dialog.dismiss()
        }

        //set default values
        showDefaultProduct()

        dialog.setOnDismissListener {
            viewModel.clearChoisesMap()
        }

        val btn1 =dialogView.findViewById<ElegantNumberButton>(R.id.number_button)
        btn1.setOnClickListener(ElegantNumberButton.OnClickListener { view: View? ->

        })
        btn1.setOnValueChangeListener { view: ElegantNumberButton?, oldValue: Int, newValue: Int ->

        }


        val addToCart=dialogView.findViewById<Button>(R.id.add_to_cart)
        addToCart.setOnClickListener {
            val map=viewModel.getChoisesMap()
            if(map.size==product.attributes.size){
                val variantId=getVariantId(map)
                if(variantId!=-1L){
                    viewModel.setStateEvent(
                        ProductStateEvent.AddProductCartEvent(
                            variantId,
                            btn1.number.toInt()
                        )
                    )
                }
            }
        }

        dialog.show()
    }

    fun getVariantValues (productVariants: ProductVariants):Map<String,String>{
        val variantValues:MutableMap<String, String> = mutableMapOf()
        productVariants.productVariantAttributeValuesProductVariant.map {
            variantValues.put(it.attributeValue.attribute,it.attributeValue.value)
        }
        return variantValues
    }

    fun getExistedOptionValue(map: Map<String, String>):List<AttributeValue>{
        var result= mutableListOf<AttributeValue>()
        val temp=mutableListOf<AttributeValue>()
        val current=mutableListOf<AttributeValue>()

        if(map.isNotEmpty()){
            map.map {choices->
                current.add(AttributeValue(choices.value,choices.key))

                product.productVariants.map {
                    val list= mutableListOf<AttributeValue>()
                    it.productVariantAttributeValuesProductVariant.map {
                        list.add(it.attributeValue)
                    }
                    list.map {//same option
                        if (it.attribute == choices.key){
                            temp.add(it)
                        }
                    }

                    if (list.containsAll(current)){
                        temp.addAll(list)
                    }

                }
                if(result.isEmpty()){
                    result=temp
                }
                result=result.intersect(temp).toMutableList()
                temp.clear()
            }
        }
        else{
            product.productVariants.map {
                it.productVariantAttributeValuesProductVariant.map {
                    result.add(it.attributeValue)
                }
            }
        }
        return result.distinct()
    }

    fun sortMapByList(map: Map<String, String>, sortedList:List<String>):Map<String,String>{
        val resultSortedMap= mutableMapOf<String,String>()
        if (map.isNotEmpty()){
            sortedList.map {
                if(map.containsKey(it)){
                    resultSortedMap[it] = map[it]!!
                }
            }
        }
        return resultSortedMap
    }

    private fun getVariantId(map: Map<String, String>):Long{
        product.let {
            for (variant in it.productVariants) {
                if (getVariantValues(variant) == map) {
                    return variant.id
                }
            }
        }
        return -1L
    }

    private fun showDetailedProduct(map: Map<String, String>){
        product.let {
            for (variant in it.productVariants){
                if(getVariantValues(variant) == map){

                    if (variant.image.isNullOrEmpty()){//variant without images
                        product.let {
                            val image= Constants.PRODUCT_IMAGE_URL +it.images.first().image
                            if (variant.offer==null){
                                setVariantDialog(
                                    "${variant.price}${Constants.DOLLAR}",
                                    variant.unit.toString(),
                                    image
                                )
                            }else{

                                val offer=variant.offer
                                var price=0.0
                                when(offer!!.type){
                                    OfferType.FIXED ->{
                                        price=variant.price-offer.newPrice!!
                                    }

                                    OfferType.PERCENTAGE ->{
                                        price=BigDecimal(variant.price-(variant.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                                    }
                                }

                                setVariantDialog(
                                    "${price}${Constants.DOLLAR}",
                                    variant.unit.toString(),
                                    image
                                )

                            }

                        }
                    }else{
                        if (variant.offer==null){
                            setVariantDialog(
                                "${variant.price}${Constants.DOLLAR}",
                                variant.unit.toString(),
                                Constants.PRODUCT_IMAGE_URL +variant.image!!
                            )
                        }else{
                            val offer=variant.offer
                            var price=0.0
                            when(offer!!.type){
                                OfferType.FIXED ->{
                                    price=variant.price-offer.newPrice!!
                                }

                                OfferType.PERCENTAGE ->{
                                    price=BigDecimal(variant.price-(variant.price*offer.percentage!!/100)).setScale(2, RoundingMode.HALF_EVEN).toDouble()
                                }
                            }

                            setVariantDialog(
                                "${price}${Constants.DOLLAR}",
                                variant.unit.toString(),
                                Constants.PRODUCT_IMAGE_URL +variant.image!!
                            )
                        }


                    }

                    return@let
                }
            }

            setVariantDialog(
                "0",
                "0",
                "0"
            )
        }
    }

    private fun showDefaultProduct(){
        product.let {
            val image= Constants.PRODUCT_IMAGE_URL +it.images.first().image

            if(it.attributes.isEmpty()){
                setVariantDialog(getPrice(),it.productVariants.first().unit.toString(),image)
            }else{
                setVariantDialog(getPrice(),"0",image)
            }

        }
    }

    override fun onItemSelected() {
        showVariantDialog()
    }

    fun initOptionsRecyclerView(recyclerview:RecyclerView){
        recyclerview.apply {
            layoutManager = LinearLayoutManager(this@ViewProductFragment.context,
                LinearLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)
            optionsRecyclerAdapter =
                OptionsAdapter(
                    this@ViewProductFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = optionsRecyclerAdapter
        }
    }

    fun setVariantDialog(price:String,quantity:String,image:String){
        dialogView.findViewById<ElegantNumberButton>(R.id.number_button).number = "1"
        dialogView.findViewById<ElegantNumberButton>(R.id.number_button).setRange(1, quantity.toInt())
        dialogView.findViewById<TextView>(R.id.product_variant_price).text=price
        dialogView.findViewById<TextView>(R.id.product_variant_quantity).text=quantity
        requestManager
            .load(image)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(dialogView.findViewById<ImageView>(R.id.product_variant_image))
    }

    fun initVariantImageRecyclerView(){
        product_recyclerview_variant_image.apply {
            layoutManager = LinearLayoutManager(this@ViewProductFragment.context,LinearLayoutManager.HORIZONTAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            variantImageRecyclerAdapter =
                VariantImageAdapter(
                    requestManager,
                    this@ViewProductFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = variantImageRecyclerAdapter
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        viewPager?.adapter=null
        mMapView.onDestroy()
    }

    override fun onItemSelected(option: String, value: String) {
        optionsRecyclerview.adapter?.notifyDataSetChanged()
        val map=viewModel.getChoisesMap()
        if(map[option]==value){
            map.remove(option)
        }else{
            map.put(option,value)
        }
        viewModel.setChoisesMap(map)
    }
    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }
}