package com.smartcity.client.ui.main.flash_notification.state

import android.os.Parcelable
import com.smartcity.client.models.City
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
        var flashDealsMap: MutableMap<String,List<FlashDeal>> = mutableMapOf(),
        var networkFlashDealsPair : Pair<String,List<FlashDeal>>? =null,
        var offerAction: List<Triple<String,Int,Int>>? = null,
        var offerActionRecyclerPosition: Int? =null,

        var defaultCity:City? =null,

        var productDiscountList: List<Product>? = null,
        var selectedProduct: Product? = null,
        var choicesMap:MutableMap<String, String>? = null
    ) : Parcelable

}