package com.smartcity.client.ui.main.cart.state

import android.os.Parcelable
import com.smartcity.client.models.Address
import com.smartcity.client.models.BillTotal
import com.smartcity.client.models.OrderType
import com.smartcity.client.models.UserInformation
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
        var addressList:List<Address> = listOf(),
        var deliveryAddress:Address? =null,
        var userInformation: UserInformation?=null
    ) : Parcelable

}