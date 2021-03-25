package com.smartcity.client.ui.main.cart.state

import android.os.Parcelable
import com.smartcity.client.models.product.Cart
import kotlinx.android.parcel.Parcelize

const val CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY = "com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState"

@Parcelize
class CartViewState(
    var cartFields:CartFields=CartFields()

) : Parcelable {
    @Parcelize
    data class CartFields(
        var cartList: Cart?=null
    ) : Parcelable


}