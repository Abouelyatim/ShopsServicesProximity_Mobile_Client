package com.smartcity.client.ui.main.blog.viewmodel

import android.os.Parcelable
import com.smartcity.client.models.BlogPost
import com.smartcity.client.models.product.Product

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












