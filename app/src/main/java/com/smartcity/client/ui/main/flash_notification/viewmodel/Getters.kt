package com.smartcity.client.ui.main.flash_notification.viewmodel

import com.smartcity.client.models.FlashDeal
import com.smartcity.client.models.product.Product
import com.smartcity.client.ui.main.account.viewmodel.AccountViewModel

fun FlashViewModel.getFlashDealsList(): List<FlashDeal> {
    getCurrentViewStateOrNew().let {
        return it.flashFields.flashDealsList
    }
}

fun FlashViewModel.getDiscountProductList(): List<Product> {
    getCurrentViewStateOrNew().let {
        return it.flashFields.productDiscountList
    }
}

fun FlashViewModel.getOfferActionRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.flashFields.offerActionRecyclerPosition
    }
}

fun FlashViewModel.getOfferAction(): List<Triple<String,Int,Int>> {
    getCurrentViewStateOrNew().let {
        return it.flashFields.offerAction
    }
}

fun FlashViewModel.getSelectedProduct(): Product? {
    getCurrentViewStateOrNew().let {
        return it.flashFields.selectedProduct
    }
}

fun FlashViewModel.getChoicesMap(): MutableMap<String, String> {
    getCurrentViewStateOrNew().let {
        return it.flashFields.choicesMap
    }
}