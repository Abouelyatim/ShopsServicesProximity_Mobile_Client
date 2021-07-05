package com.smartcity.client.ui.main.flash_notification.viewmodel

import com.smartcity.client.models.FlashDeal
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.main.account.viewmodel.AccountViewModel
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.viewmodel.ProductViewModel

fun FlashViewModel.setFlashDealsList(list: List<FlashDeal>) {
    val update = getCurrentViewStateOrNew()
    update.flashFields.flashDealsList=list
    setViewState(update)
}

fun FlashViewModel.setDiscountProductList(list: List<Product>) {
    val update = getCurrentViewStateOrNew()
    update.flashFields.productDiscountList=list
    setViewState(update)
}

fun FlashViewModel.setOfferActionList(orderActionList: List<Triple<String,Int,Int>>){
    val update = getCurrentViewStateOrNew()
    update.flashFields.offerAction = orderActionList
    setViewState(update)
}

fun FlashViewModel.setOfferActionRecyclerPosition(postion:Int){
    val update = getCurrentViewStateOrNew()
    update.flashFields.offerActionRecyclerPosition=postion
    setViewState(update)
}

fun FlashViewModel.clearFlashList(){
    val update = getCurrentViewStateOrNew()
    update.flashFields.flashDealsList= listOf()
    setViewState(update)
}

fun FlashViewModel.clearProductList(){
    val update = getCurrentViewStateOrNew()
    update.flashFields.productDiscountList= listOf()
    setViewState(update)
}

fun FlashViewModel.setSelectedProduct(product: Product) {
    val update = getCurrentViewStateOrNew()
    update.flashFields.selectedProduct= product
    setViewState(update)
}

fun FlashViewModel.setChoicesMap(map: MutableMap<String, String>) {
    val update = getCurrentViewStateOrNew()
    update.flashFields.choicesMap= map
    setViewState(update)
}

fun FlashViewModel.clearChoicesMap(){
    val update = getCurrentViewStateOrNew()
    update.flashFields.choicesMap= mutableMapOf()
    setViewState(update)
}