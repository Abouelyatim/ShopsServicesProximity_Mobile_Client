package com.smartcity.client.ui.main.custom_category

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Attribute
import com.smartcity.client.models.product.Product
import com.smartcity.client.models.product.ProductVariants
import com.smartcity.client.repository.main.CustomCategoryRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Loading

import com.smartcity.client.ui.main.custom_category.state.CustomCategoryStateEvent
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryStateEvent.*
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState
import com.smartcity.client.ui.main.custom_category.state.CustomCategoryViewState.*
import com.smartcity.client.util.AbsentLiveData

import javax.inject.Inject
import kotlin.collections.HashSet

@MainScope
class CustomCategoryViewModel
@Inject
constructor(
    val customCategoryRepository: CustomCategoryRepository,
    val sessionManager: SessionManager
): BaseViewModel<CustomCategoryStateEvent, CustomCategoryViewState>() {

    override fun handleStateEvent(stateEvent: CustomCategoryStateEvent): LiveData<DataState<CustomCategoryViewState>> {

        when(stateEvent){
            is CustomCategoryMain ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    customCategoryRepository.attemptCustomCategoryMain(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()

            }
            is CreateCustomCategory ->{
                return sessionManager.cachedToken.value?.let { authToken ->

                    customCategoryRepository.attemptCreateCustomCategory(
                        authToken.account_pk!!.toLong(),
                        stateEvent.name
                    )
                }?: AbsentLiveData.create()
            }
            is DeleteCustomCategory -> {
                return customCategoryRepository.attemptdeleteCustomCategory(
                    stateEvent.id
                )
            }
            is UpdateCustomCategory -> {
                return customCategoryRepository.attemptUpdateCustomCategory(
                    stateEvent.id,
                    stateEvent.name,
                    stateEvent.provider
                )
            }

            is CreateProduct ->{
                return customCategoryRepository.attemptCreateProduct(
                    stateEvent.product,
                    stateEvent.productImagesFile,
                    stateEvent.variantesImagesFile,
                    stateEvent.productObject
                )
            }
            is UpdateProduct ->{
                return customCategoryRepository.attemptUpdateProduct(
                    stateEvent.product,
                    stateEvent.productImagesFile,
                    stateEvent.variantesImagesFile,
                    stateEvent.productObject
                )
            }

            is DeleteProduct ->{
                return customCategoryRepository.attemptDeleteProduct(
                    stateEvent.id
                )
            }

            is ProductMain ->{
                return customCategoryRepository.attemptProductMain(
                    stateEvent.id
                )
            }
            is None -> {
                return liveData {
                    emit(
                        DataState(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }

    }

    override fun initNewViewState(): CustomCategoryViewState {
        return CustomCategoryViewState()
    }

    fun cancelActiveJobs(){
        customCategoryRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun setCustomCategoryFields(customCategoryFields: CustomCategoryFields){
        val update = getCurrentViewStateOrNew()
        if(update.customCategoryFields == customCategoryFields){
            return
        }
        update.customCategoryFields = customCategoryFields
        setViewState(update)
    }
    fun getCustomCategoryFields():List<CustomCategory>{
        getCurrentViewStateOrNew().let {
            return it.customCategoryFields.customCategoryList
        }
    }

    fun setSelectedCustomCategory(selectedCustomCategory: CustomCategory){
        val update = getCurrentViewStateOrNew()
        update.selectedCustomCategory.customCategory = selectedCustomCategory
        setViewState(update)
    }
    fun setProductFields(productFields: ProductFields){
        val update = getCurrentViewStateOrNew()
        update.productFields = productFields
        setViewState(update)
    }



    fun getSelectedCustomCategory():CustomCategory?{
        getCurrentViewStateOrNew().let {
            return it.selectedCustomCategory.customCategory
        }
    }

    fun setNewOption(attribute: Attribute?){
        val update = getCurrentViewStateOrNew()
        update.newOption.attribute = attribute
        setViewState(update)
    }



    fun getNewOption():Attribute?{
        getCurrentViewStateOrNew().let {
            return it.newOption.attribute
        }
    }

    fun setOptionList(attribute: Attribute){
        val update = getCurrentViewStateOrNew()
        val optionList= update.productFields.attributeList
        optionList.find { it.name.equals(attribute.name) }.let {
            if (it != null) {
                it.attributeValues=attribute.attributeValues
            }else{
                optionList.add(attribute)
            }
        }
        update.productFields.attributeList=optionList
        setViewState(update)
    }



    fun setOptionList(attributeSet: HashSet <Attribute>){
        val update = getCurrentViewStateOrNew()
        update.productFields.attributeList=attributeSet
        setViewState(update)
    }

    fun getOptionList():HashSet <Attribute>{
        getCurrentViewStateOrNew().let {
            return it.productFields.attributeList
        }
    }



    fun setProductVariantsList(productVariants: MutableList<ProductVariants>){
        val update = getCurrentViewStateOrNew()
        update.productFields.productVariantList=productVariants
        setViewState(update)
    }

    fun getProductVariantsList():List<ProductVariants>{
        getCurrentViewStateOrNew().let {
            return it.productFields.productVariantList
        }
    }

    fun setCopyImage(uri: Uri?){
        val update = getCurrentViewStateOrNew()
        update.copyImage.copyImage=uri
        setViewState(update)
    }

    fun getCopyImage():Uri?{
        getCurrentViewStateOrNew().let {
            return it.copyImage.copyImage
        }
    }

    fun setProductList(productList: ProductList){
        val update = getCurrentViewStateOrNew()
        update.productList=productList
        setViewState(update)
    }

    fun getProductList():List<Product>{
        getCurrentViewStateOrNew().let {
            return it.productList.products
        }
    }

    fun isEmptyProductFields():Boolean{
        val update = getCurrentViewStateOrNew()
        if (update.productFields.name==""){
            return true
        }
        return false
    }
    fun setProductImageList(image: Uri){
        val update = getCurrentViewStateOrNew()
        update.productFields.productImageList.add(image)
        setViewState(update)
    }

    fun setProductImageList(images: MutableList<Uri>){
        val update = getCurrentViewStateOrNew()
        update.productFields.productImageList=images
        setViewState(update)
    }


    fun getProductImageList():List<Uri>?{
        getCurrentViewStateOrNew().let {
            return it.productFields.productImageList
        }
    }




    fun setSelectedProductVariant(productVariants: ProductVariants?){
        val update = getCurrentViewStateOrNew()
        update.selectedProductVariant.variante = productVariants
        setViewState(update)
    }



    fun getSelectedProductVariant():ProductVariants?{
        getCurrentViewStateOrNew().let {
            return it.selectedProductVariant.variante
        }
    }

    fun setViewProductFields(product: Product){
        val update = getCurrentViewStateOrNew()
        update.viewProductFields.product = product
        setViewState(update)
    }

    fun getViewProductFields():Product?{
        getCurrentViewStateOrNew().let {
            return it.viewProductFields.product
        }
    }



    fun setChoisesMap(map: MutableMap<String, String>){
        val update = getCurrentViewStateOrNew()
        update.choisesMap.choises = map
        setViewState(update)
    }

    fun getChoisesMap():MutableMap<String, String>{
        getCurrentViewStateOrNew().let {
            return it.choisesMap.choises
        }
    }
    fun clearChoisesMap(){
        val update = getCurrentViewStateOrNew()
        update.choisesMap=ChoisesMap()
        setViewState(update)
    }




    fun clearProductFields(){
        val update = getCurrentViewStateOrNew()
        update.newOption= NewOption()
        update.productFields= ProductFields()
        update.selectedProductVariant= SelectedProductVariant()
        setViewState(update)
    }

    fun clearViewProductFields(){
        val update = getCurrentViewStateOrNew()
        update.viewProductFields= ViewProductFields()
        setViewState(update)
    }

    fun clearProductList(){
        val update = getCurrentViewStateOrNew()
        update.productList=ProductList()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}










