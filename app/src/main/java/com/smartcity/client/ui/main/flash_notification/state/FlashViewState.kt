package com.smartcity.client.ui.main.flash_notification.state

import android.os.Parcelable
import com.smartcity.client.models.FlashDeal
import com.smartcity.client.models.product.Product
import kotlinx.android.parcel.Parcelize

const val CUSTOM_FLASH_VIEW_STATE_BUNDLE_KEY = "com.smartcity.client.ui.main.flash_notification.state.FlashViewState"

@Parcelize
class FlashViewState(
    var flashFields: FlashFields = FlashFields()
    ) : Parcelable {

    @Parcelize
    data class FlashFields(
        var flashDealsList: List<FlashDeal> = listOf(),
        var offerAction: List<Triple<String,Int,Int>> = listOf(),
        var offerActionRecyclerPosition: Int =0,

        var productDiscountList: List<Product> = listOf(),
        var selectedProduct: Product? = null,
        var choicesMap:MutableMap<String, String> = mutableMapOf()
    ) : Parcelable

}