package com.smartcity.client.ui.main.blog.viewmodel

import android.os.Parcelable
import com.smartcity.client.models.BlogPost
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.main.blog.state.ProductViewState

fun ProductViewModel.setQueryExhausted(isExhausted: Boolean){
    val update = getCurrentViewStateOrNew()
    update.productFields.isQueryExhausted = isExhausted
    setViewState(update)
}

fun ProductViewModel.setQueryInProgress(isInProgress: Boolean){
    val update = getCurrentViewStateOrNew()
    update.productFields.isQueryInProgress = isInProgress
    setViewState(update)
}

fun ProductViewModel.clearLayoutManagerState(){
    val update = getCurrentViewStateOrNew()
    update.productFields.layoutManagerState = null
    setViewState(update)

}
fun ProductViewModel.setLayoutManagerState(layoutManagerState: Parcelable){
    val update = getCurrentViewStateOrNew()
    update.productFields.layoutManagerState = layoutManagerState
    setViewState(update)
}

fun ProductViewModel.setProductListData(productList: List<Product>){
    val update = getCurrentViewStateOrNew()
    update.productFields.productList = productList
    setViewState(update)
}

fun ProductViewModel.clearProductListData(){
    val update = getCurrentViewStateOrNew()
    update.productFields.productList = listOf()
    setViewState(update)
}

fun ProductViewModel.clearQuery(){
    val update = getCurrentViewStateOrNew()
    update.productFields.searchQuery = ""
    setViewState(update)
}

fun ProductViewModel.clearNewProductListData(){
    val update = getCurrentViewStateOrNew()
    update.productFields.newProductList = listOf()
    setViewState(update)
}

fun ProductViewModel.setQuery(query: String){
    val update = getCurrentViewStateOrNew()
    update.productFields.searchQuery = query
    setViewState(update)
}

fun ProductViewModel.setChoisesMap(map: MutableMap<String, String>){
    val update = getCurrentViewStateOrNew()
    update.choisesMap.choises = map
    setViewState(update)
}

fun ProductViewModel.clearChoisesMap(){
    val update = getCurrentViewStateOrNew()
    update.choisesMap= ProductViewState.ChoisesMap()
    setViewState(update)
}

fun ProductViewModel.setViewProductFields(product: Product){
    val update = getCurrentViewStateOrNew()
    update.viewProductFields.product = product
    setViewState(update)
}

fun ProductViewModel.setStoreCustomCategoryLists(list:List<CustomCategory>){
    val update = getCurrentViewStateOrNew()
    update.viewProductFields.storeCustomCategoryList = list
    setViewState(update)
}

fun ProductViewModel.setStoreProductLists(list:List<Product>){
    val update = getCurrentViewStateOrNew()
    update.viewProductFields.storeProductList = list
    setViewState(update)
}

fun ProductViewModel.clearStoreProductLists(){
    val update = getCurrentViewStateOrNew()
    update.viewProductFields.storeProductList = listOf()
    setViewState(update)
}

fun ProductViewModel.clearStoreInformation(){
    val update = getCurrentViewStateOrNew()
    update.viewProductFields.storeCustomCategoryList = listOf()
    update.viewProductFields.customCategoryRecyclerPosition = 0
    update.viewProductFields.storeProductList = listOf()
    setViewState(update)
}

fun ProductViewModel.setCustomCategoryRecyclerPosition(position:Int){
    val update = getCurrentViewStateOrNew()
    update.viewProductFields.customCategoryRecyclerPosition = position
    setViewState(update)
}

