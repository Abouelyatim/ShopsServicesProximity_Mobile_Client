package com.smartcity.client.ui.main.blog.viewmodel

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

fun ProductViewModel.clearProductSearchListData(){
    val update = getCurrentViewStateOrNew()
    update.searchProductFields.productSearchList = listOf()
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

fun ProductViewModel.setGridOrListView(bool:Boolean){
    val update = getCurrentViewStateOrNew()
    update.productFields.gridOrListView = bool
    setViewState(update)
}

fun ProductViewModel.setQueryExhaustedSearch(isExhausted: Boolean){
    val update = getCurrentViewStateOrNew()
    update.searchProductFields.isQuerySearchExhausted = isExhausted
    setViewState(update)
}

fun ProductViewModel.setQueryInProgressSearch(isInProgress: Boolean){
    val update = getCurrentViewStateOrNew()
    update.searchProductFields.isQuerySearchInProgress = isInProgress
    setViewState(update)
}

fun ProductViewModel.setProductListDataSearch(productList: List<Product>){
    val update = getCurrentViewStateOrNew()
    update.searchProductFields.productSearchList = productList
    setViewState(update)
}

fun ProductViewModel.clearProductListDataSearch(){
    val update = getCurrentViewStateOrNew()
    update.searchProductFields.productSearchList = listOf()
    setViewState(update)
}

fun ProductViewModel.setQuerySearch(query: String){
    val update = getCurrentViewStateOrNew()
    update.searchProductFields.searchQuery = query
    setViewState(update)
}

fun ProductViewModel.setGridOrListViewSearch(bool:Boolean){
    val update = getCurrentViewStateOrNew()
    update.searchProductFields.gridOrListSearchView = bool
    setViewState(update)
}