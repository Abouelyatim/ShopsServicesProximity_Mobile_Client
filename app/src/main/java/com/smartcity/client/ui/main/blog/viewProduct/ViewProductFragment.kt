package com.smartcity.client.ui.main.blog.viewProduct

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton
import com.smartcity.client.R
import com.smartcity.client.models.product.AttributeValue
import com.smartcity.client.models.product.Product
import com.smartcity.client.models.product.ProductVariants
import com.smartcity.client.ui.main.blog.BaseBlogFragment
import com.smartcity.client.ui.main.blog.state.ProductStateEvent
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.viewProduct.adapters.OptionsAdapter
import com.smartcity.client.ui.main.blog.viewProduct.adapters.ValuesAdapter
import com.smartcity.client.ui.main.blog.viewProduct.adapters.VariantImageAdapter
import com.smartcity.client.ui.main.blog.viewProduct.adapters.ViewPagerAdapter
import com.smartcity.client.ui.main.blog.viewmodel.*
import com.smartcity.client.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.util.Constants
import com.smartcity.client.util.SuccessHandling
import com.smartcity.client.util.TopSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_view_product.*

import javax.inject.Inject

class ViewProductFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseBlogFragment(R.layout.fragment_view_product),
    OptionsAdapter.Interaction,
    VariantImageAdapter.Interaction
{
    private lateinit var dialogView: View
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var product: Product
    private lateinit var  variantImageRecyclerAdapter: VariantImageAdapter
    private lateinit var  optionsRecyclerAdapter: OptionsAdapter
    private lateinit var optionsRecyclerview: RecyclerView

    val viewModel: ProductViewModel by viewModels{
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
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
    override fun cancelActiveJobs(){
        viewModel.cancelActiveJobs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        stateChangeListener.expandAppBar()

        viewModel.getViewProductFields()?.let {
            product=it
        }


        initViewPager()
        setPrice(product_price)
        setName()
        setOptions()
        setDescription()
        variantsDialog()
        subscribeObservers()
    }

    private fun initViewPager() {
        viewPager = activity!!.findViewById(R.id.view_pager)
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
        val prices =product.productVariants.map { productVariant -> productVariant.price }
        if(prices.max() != prices.min()){
            return "${prices.min()}${Constants.DINAR_ALGERIAN} - ${prices.max()}${Constants.DINAR_ALGERIAN}"
        }else{
            return "${prices.max()}${Constants.DINAR_ALGERIAN}"
        }
    }

    private fun setName() {
        product_name.text=product.name
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
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            stateChangeListener.onDataStateChange(dataState)
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
        val dialog = Dialog(context!!, android.R.style.Theme_Light)
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
                            setVariantDialog(
                                "${variant.price}${Constants.DINAR_ALGERIAN}",
                                variant.unit.toString(),
                                image
                            )
                        }
                    }else{
                        setVariantDialog(
                            "${variant.price}${Constants.DINAR_ALGERIAN}",
                            variant.unit.toString(),
                            Constants.PRODUCT_IMAGE_URL +variant.image!!
                        )
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
        viewPager.adapter=null
    }

    override fun onItemSelected(option: String, value: String) {
        optionsRecyclerview.adapter!!.notifyDataSetChanged()
        val map=viewModel.getChoisesMap()
        if(map[option]==value){
            map.remove(option)
        }else{
            map.put(option,value)
        }
        viewModel.setChoisesMap(map)
    }
}