package com.smartcity.client.ui.main.cart.state

import android.os.Parcelable
import com.smartcity.client.models.*
import com.smartcity.client.models.product.Cart
import com.smartcity.provider.models.Policy
import kotlinx.android.parcel.Parcelize

const val CUSTOM_CATEGORY_VIEW_STATE_BUNDLE_KEY = "com.smartcity.provider.ui.main.custom_category.state.CustomCategoryViewState"

@Parcelize
class CartViewState(
    var cartFields:CartFields=CartFields(),
    var orderFields:OrderFields=OrderFields()

) : Parcelable {
    @Parcelize
    data class CartFields(
        var cartList: Cart?=null
    ) : Parcelable

    @Parcelize
    data class OrderFields(
        var storePolicy: Policy?=null,
        var selectedCartProduct: Cart?=null,
        var total:BillTotal?=null,
        var orderType: OrderType?=null,
        var addressList:List<Address>? = null,
        var deliveryAddress:Address? =null,
        var defaultCity : City? =null,
        var apartmentNumber:String?=null,
        var businessName:String?=null,
        var doorCodeName:String?=null,
        var userInformation: UserInformation?=null
    ) : Parcelable
}