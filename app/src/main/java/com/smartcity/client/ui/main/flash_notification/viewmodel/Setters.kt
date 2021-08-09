package com.smartcity.client.ui.main.flash_notification.viewmodel

import com.smartcity.client.models.City
import com.smartcity.client.models.FlashDeal
import com.smartcity.client.models.product.Product

fun FlashViewModel.setFlashDealsList(pair: Pair<String,List<FlashDeal>>) {
    val update = getCurrentViewStateOrNew()
    update.flashFields.flashDealsMap.put(pair.first,pair.second)
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

fun FlashViewModel.setDefaultCity(city : City) {
    val update = getCurrentViewStateOrNew()
    update.flashFields.defaultCity= city
    setViewState(update)
}