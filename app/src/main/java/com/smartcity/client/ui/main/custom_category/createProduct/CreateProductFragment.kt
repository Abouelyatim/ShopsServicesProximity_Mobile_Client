package com.smartcity.client.ui.main.custom_category.createProduct

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import com.smartcity.client.R
import com.smartcity.client.models.product.Image
import com.smartcity.client.models.product.Product
import com.smartcity.client.models.product.ProductVariants
import com.smartcity.client.ui.*
import com.smartcity.client.ui.main.custom_category.BaseCustomCategoryFragment
import com.smartcity.client.ui.main.custom_category.CustomCategoryViewModel
import com.smartcity.client.ui.main.custom_category.state.CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryStateEvent
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState.ProductFields
import com.smartcity.client.util.*
import com.smartcity.client.util.Constants.Companion.LOCAL_STORAGE_DIRECTORY
import kotlinx.android.synthetic.main.fragment_create_product.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.lang.Exception
import java.util.*
import javax.inject.Inject


class CreateProductFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
): BaseCustomCategoryFragment(R.layout.fragment_create_product),
    VarianteAdapter.Interaction,
    ProductImageAdapter.Interaction,
    ProductImageAdapter.InteractionAdd
{
    private lateinit var varianteRecyclerAdapter: VarianteAdapter
    private lateinit var productImageRecyclerAdapter: ProductImageAdapter
    private var Observer=true
    var viewStateObserver=true
    var ACTION=-1
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

        addOption()

        if(viewModel.getViewProductFields()!=null)//detect if update or create
            ACTION=ActionConstants.UPDATE
        else ACTION= ActionConstants.CREATE

        if(ACTION==ActionConstants.UPDATE){
            if (viewModel.isEmptyProductFields()){//if fields product fields are not set
                setOldProductFields()
            }
            product_save_button.text = "Update"
        }

        initVarianteRecyclerView()
        initProductImageRecyclerView()
        subscribeObservers()
        product_save_button.setOnClickListener {
            createUpdateProduct()
        }

    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer{ dataState ->
            stateChangeListener.onDataStateChange(dataState)

            //after successful product creation fire network request to update product list
            if(dataState != null){
                dataState.data?.let { data ->
                    data.response?.peekContent()?.let{ response ->

                        when(response.message){
                            SuccessHandling.PRODUCT_UPDATE_DONE->{
                                removeTempFiles()
                                updateProductList()
                            }

                            SuccessHandling.PRODUCT_CREATION_DONE->{
                                removeTempFiles()
                                updateProductList()
                            }
                            else ->{
                            }
                        }
                        //if(!data.response.hasBeenHandled){
                        //  }

                    }
                }

                //handle request response to update product list
                dataState.data?.let { data ->
                    data.data?.let{
                        it.getContentIfNotHandled()?.let{
                            it.productList.let {
                                viewModel.setProductList(it)
                            }
                            viewModel.clearProductFields()
                            findNavController().navigate(R.id.action_createProductFragment_to_productFragment)
                        }

                    }

                }
            }

        })


        productImageRecyclerAdapter.getListLivedata().observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            it?.let {
                viewModel.setProductImageList(it.toMutableList())
            }

        })

        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            if(Observer){
                viewState.productFields?.let {productFields ->
                    if (viewStateObserver){
                        setProductProperties(
                            productFields.name,
                            productFields.description,
                            productFields.price,
                            productFields.quantity
                        )
                    }

                }
            }

            productImageRecyclerAdapter.submitList(viewModel.getProductImageList())
            varianteRecyclerAdapter.submitList(viewModel.getProductVariantsList())

            if(viewModel.getProductVariantsList().isEmpty()){
                setUiVisibility(View.VISIBLE)
            }else{
                setUiVisibility(View.GONE)
            }
        })
    }

    private fun setOldProductFields() {
        if(viewModel.getViewProductFields()!!.attributes.isEmpty()){//product without variances
            viewModel.setProductFields(
                ProductFields(
                    viewModel.getViewProductFields()!!.description,
                    viewModel.getViewProductFields()!!.name,
                    viewModel.getViewProductFields()!!.productVariants.first().price.toString(),
                    viewModel.getViewProductFields()!!.productVariants.first().unit.toString(),
                    viewModel.getViewProductFields()!!.images.map { image -> Uri.parse(Constants.PRODUCT_IMAGE_URL+image.image) }.toMutableList(),
                    mutableListOf(),
                    viewModel.getViewProductFields()!!.attributes.toHashSet()
                )
            )
        }else{//product with variances
            viewModel.setProductFields(
                ProductFields(
                    viewModel.getViewProductFields()!!.description,
                    viewModel.getViewProductFields()!!.name,
                    "",
                    "",
                    viewModel.getViewProductFields()!!.images.map { image -> Uri.parse(Constants.PRODUCT_IMAGE_URL+image.image) }.toMutableList(),
                    viewModel.getViewProductFields()!!.productVariants
                        .map { variant ->
                            val imageUri=Uri.parse(Constants.PRODUCT_IMAGE_URL+variant.image)
                            variant.imageUri=imageUri
                            variant
                        }.toMutableList(),
                    viewModel.getViewProductFields()!!.attributes.toHashSet()
                )
            )
        }
    }

    private fun getUriName(uri:Uri):String{
        return RingtoneManager.getRingtone(context, uri).getTitle(context)
    }

    private fun updateProductOptionList(){
        try {
            viewStateObserver=false
            val options= Collections.synchronizedSet(viewModel.getOptionList())
            options.map { option->
                option.attributeValues.map {optionValue->
                    var found=false
                    viewModel.getProductVariantsList().map { variant->
                        variant.productVariantAttributeValuesProductVariant.map {productVariantAttributeValuesProductVariant->
                            val variantOption=productVariantAttributeValuesProductVariant.attributeValue.value
                            if (variantOption == optionValue.value){
                                found=true
                            }
                        }
                    }

                    if (!found){//if there is no option value inside variant so we deleted from option list
                        val attrebute=Collections.synchronizedSet(viewModel.getOptionList())

                        attrebute.map {
                            it.attributeValues.remove(optionValue)
                            viewModel.setOptionList(it)
                        }
                    }
                }
            }

            //if we delete all value of an option so we delete the option
            val attributeList=viewModel.getOptionList().toMutableList()
            val attributeListIterator=attributeList.iterator()
            while (attributeListIterator.hasNext()){
                val item=attributeListIterator.next()
                if (item.attributeValues.isEmpty()){
                    attributeList.remove(item)
                }
            }

            viewModel.setOptionList(attributeList.toHashSet())
            viewStateObserver=true
        }catch (e:Exception){

        }

    }
    private fun constructFullProductObject():Product{
        var images:List<Image>? = listOf()


            images=viewModel.getProductImageList()?.let {
                it.map {
                    Image(getUriName(it))
                }
            }


        updateProductOptionList()

        when(ACTION){

            ActionConstants.UPDATE->{
                return Product(
                    viewModel.getViewProductFields()!!.id,
                    input_product_description.text.toString(),
                    input_product_name.text.toString(),
                    images!!,
                    viewModel.getProductVariantsList(),
                    viewModel.getOptionList(),
                    viewModel.getSelectedCustomCategory()!!.pk)
            }

            ActionConstants.CREATE->{
                return Product(
                    -1,
                    input_product_description.text.toString(),
                    input_product_name.text.toString(),
                    images!!,
                    viewModel.getProductVariantsList(),
                    viewModel.getOptionList(),
                    viewModel.getSelectedCustomCategory()!!.pk)
            }
            else->{
                return Product(
                    -1,
                    input_product_description.text.toString(),
                    input_product_name.text.toString(),
                    images!!,
                    viewModel.getProductVariantsList(),
                    viewModel.getOptionList(),
                    viewModel.getSelectedCustomCategory()!!.pk)
            }
        }

    }

    private fun constructProductObjectWithOutVariant(product: Product):Product{
        if(input_product_price.text.toString().isNotEmpty().and(input_product_quantity.text.toString().isNotEmpty())){
            val productVariant=ProductVariants(
                listOf(),
                null,
                input_product_price.text.toString().toDouble(),
                input_product_quantity.text.toString().toInt(),
                null
            )
            val productVariantList= listOf<ProductVariants>(productVariant)
            product.productVariants=productVariantList
        }
        return product
    }

    private fun createUpdateProduct() {
         var product= constructFullProductObject()

        if (viewModel.getProductVariantsList().isEmpty()){
            product=constructProductObjectWithOutVariant(product)
        }

        val gson = Gson()
        val productJson: String = gson.toJson(product)
        val requestBody=RequestBody.create(
            MediaType.parse("application/json"),
            productJson
        )

        val productImagesFileBody=uriToMultipartBody(
            viewModel.getProductImageList(),
            "productImagesFile")

        if(product.images.isEmpty()){
            showErrorDialog(ErrorHandling.ERROR_MUST_SELECT_IMAGE)
            return
        }


        val variantesImagesUri: MutableList<Uri> = mutableListOf()
        viewModel.getProductVariantsList()?.let {
            it.map {
                it.imageUri?.let {
                    variantesImagesUri.add(it)
                }
            }
        }

        val variantesImagesFile=uriToMultipartBody(
            variantesImagesUri,
            "variantesImagesFile"
        )



        when(ACTION){

            ActionConstants.CREATE ->{
                viewModel.setStateEvent(
                    CustomCategoryStateEvent.CreateProduct(
                        requestBody,
                        productImagesFileBody,
                        variantesImagesFile,
                        product
                    )
                )
            }

            ActionConstants.UPDATE ->{
                viewModel.setStateEvent(
                    CustomCategoryStateEvent.UpdateProduct(
                        requestBody,
                        productImagesFileBody,
                        variantesImagesFile,
                        product
                    )
                )
            }

        }

    }


    private fun uriToMultipartBody(uriList:List<Uri>?,multipartBodyName:String):MutableList<MultipartBody.Part>{
        val productImagesFileBody: MutableList<MultipartBody.Part> = mutableListOf()
        uriList?.let {
            it.map {
                it.path?.let{filePath ->
                    val imageFile = File(filePath)
                    if(imageFile.exists()){
                        Log.d(TAG, "CreateProductFragment, imageFile: file: ${imageFile}")
                        val requestBody =
                            RequestBody.create(
                                MediaType.parse("image/*"),
                                imageFile
                            )

                        productImagesFileBody.add(
                            MultipartBody.Part.createFormData(
                                multipartBodyName,
                                imageFile.name,
                                requestBody
                            )
                        )
                    }
                }
            }
        }
        return productImagesFileBody
    }

    private fun initProductImageRecyclerView() {
        recyclerview_product_image.apply {
            layoutManager = GridLayoutManager(this@CreateProductFragment.context, 4, GridLayoutManager.VERTICAL, false)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            productImageRecyclerAdapter =
                ProductImageAdapter(
                    requestManager,
                    this@CreateProductFragment,
                    this@CreateProductFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = productImageRecyclerAdapter
            productImageRecyclerAdapter.getItemTouchHelper()!!.attachToRecyclerView(recyclerview_product_image)
        }
    }

    private fun pickFromGallery() {
        ImagePicker.with(this)
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .saveDir(File(Environment.getExternalStorageDirectory(), LOCAL_STORAGE_DIRECTORY))
            .galleryMimeTypes(  //Exclude gif images
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg"
                )
            )
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "CROP: RESULT OK")

            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            fileUri?.let {
                viewModel.setProductImageList(
                    it
                )
            }

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            showErrorDialog(ErrorHandling.ERROR_SOMETHING_WRONG_WITH_IMAGE)
        }
    }

    private fun updateProductList(){
        viewModel.setStateEvent(
            CustomCategoryStateEvent.ProductMain(
                viewModel.getSelectedCustomCategory()!!.pk.toLong()
            ))
    }

    fun setUiVisibility(visible: Int){
        add_option_button.visibility=visible
        price_quntity_inputs.visibility=visible
    }

    private fun removeTempFiles() {
        val dir = File(
            Environment.getExternalStorageDirectory(), LOCAL_STORAGE_DIRECTORY
        )
        if (dir.isDirectory) {
            val children = dir.list()
            children.map {child->
                File(dir, child).delete()
            }
        }
    }

    fun setProductProperties(name: String?,
                             description: String?,
                             price: String,
                             quentity:String){
        input_product_name.setText(name)
        input_product_description.setText(description)
        input_product_price.setText(price)
        input_product_quantity.setText(quentity)

    }

    fun showErrorDialog(errorMessage: String){
        stateChangeListener.onDataStateChange(
            DataState(
                Event(StateError(Response(errorMessage, ResponseType.Dialog()))),
                Loading(isLoading = false),
                Data(Event.dataEvent(null), null)
            )
        )
    }
    fun initVarianteRecyclerView(){
        variant_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@CreateProductFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(0)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            varianteRecyclerAdapter =
                VarianteAdapter(
                    requestManager,
                    this@CreateProductFragment
                )
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                }
            })
            adapter = varianteRecyclerAdapter
        }

    }

    private fun addOption() {
        add_option_button.setOnClickListener {
            findNavController().navigate(R.id.action_createProductFragment_to_createOptionFragment)
        }
    }

    override fun onItemSelected(position: Int, item: ProductVariants,action:Int) {
        when(action){
            ActionConstants.SELECTED->{
                viewStateObserver=false
                viewModel.setSelectedProductVariant(item)
                findNavController().navigate(R.id.action_createProductFragment_to_variantFragment)
            }

            ActionConstants.DELETE->{
                val callback: AreYouSureCallback = object: AreYouSureCallback {
                    override fun proceed() {

                        val list=viewModel.getProductVariantsList().toMutableList()
                        list.remove(item)
                        viewModel.setProductVariantsList(list)

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


        }

    }

    override fun onItemSelected(position: Int, item: Uri) {
        showImageDialog(item)
    }

    override fun onItemAddSelected() {
        if(stateChangeListener.isStoragePermissionGranted()){
            pickFromGallery()
        }
    }


    fun showImageDialog(uri: Uri){
        val dialog = Dialog(context!!, android.R.style.Theme_Light)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_image_view)
        val image = dialog.findViewById<ImageView>(R.id.image_view)
        requestManager
            .load(uri)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(image)

        val buttonDelete=dialog.findViewById<Button>(R.id.delete_image)
        val buttonNavigate=dialog.findViewById<ImageView>(R.id.navigate)

        buttonNavigate.setOnClickListener {
            dialog.dismiss()
        }
        buttonDelete.setOnClickListener {
            val callback: AreYouSureCallback = object: AreYouSureCallback {
                override fun proceed() {
                    viewModel.getProductImageList()?.let {
                        val list=it.toMutableList()
                        list.remove(uri)
                        Observer=false
                        viewModel.setProductImageList(list)
                        Observer=true
                        dialog.dismiss()
                    }

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

        dialog.show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.setProductFields(
            ProductFields(
                input_product_description.text.toString(),
                input_product_name.text.toString(),
                input_product_price.text.toString(),
                input_product_quantity.text.toString(),
                viewModel.getProductImageList()!!.toMutableList(),
                viewModel.getProductVariantsList()!!.toMutableList(),
                viewModel.getOptionList()!!.toHashSet()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // clear references (can leak memory)
        variant_recyclerview.adapter = null
        recyclerview_product_image.adapter=null
        productImageRecyclerAdapter.clearItemTouchHelper()
        viewModel.setProductFields(
            ProductFields(
                input_product_description.text.toString(),
                input_product_name.text.toString(),
                input_product_price.text.toString(),
                input_product_quantity.text.toString(),
                viewModel.getProductImageList()!!.toMutableList(),
                viewModel.getProductVariantsList()!!.toMutableList(),
                viewModel.getOptionList()!!.toHashSet()
            )
        )
    }

}