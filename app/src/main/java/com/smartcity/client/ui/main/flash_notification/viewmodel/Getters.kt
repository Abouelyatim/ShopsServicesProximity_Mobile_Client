package com.smartcity.client.ui.main.flash_notification.viewmodel

import com.smartcity.client.models.FlashDeal
import com.smartcity.client.models.product.Product

fun FlashViewModel.getFlashDealsList(): List<FlashDeal> {
    getCurrentViewStateOrNew().let {
        return it.flashFields.flashDealsList?: listOf()
    }
}

fun FlashViewModel.getDiscountProductList(): List<Product> {
    getCurrentViewStateOrNew().let {
            return it.flashFields.productDiscountList?: listOf()
    }
}

fun FlashViewModel.getOfferActionRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.flashFields.offerActionRecyclerPosition?:0
    }
}

fun FlashViewModel.getOfferAction(): List<Triple<String,Int,Int>> {
    getCurrentViewStateOrNew().let {
        return it.flashFields.offerAction?: listOf()
    }
}

fun FlashViewModel.getSelectedProduct(): Product? {
    getCurrentViewStateOrNew().let {
        return it.flashFields.selectedProduct
    }
}

fun FlashViewModel.getChoicesMap(): MutableMap<String, String> {
    getCurrentViewStateOrNew().let {
        return it.flashFields.choicesMap?: mutableMapOf()
    }
}