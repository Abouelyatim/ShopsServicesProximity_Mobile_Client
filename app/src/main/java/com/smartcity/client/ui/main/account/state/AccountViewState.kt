package com.smartcity.client.ui.main.account.state

import android.os.Parcelable
import com.smartcity.client.models.*
import com.smartcity.client.models.product.Cart
import com.smartcity.client.models.product.Product
import com.smartcity.provider.models.Policy
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"

@Parcelize
class AccountViewState(
    var userInformation: UserInformation?=null,
    var addressList:List<Address> = listOf(),
    var orderFields:OrderFields=OrderFields(),
    var viewOrderFields: ViewOrderFields =ViewOrderFields()

) : Parcelable{

    @Parcelize
    data class OrderFields(
        var ordersList:List<Order> = ArrayList<Order>(),
        var orderAction: List<Triple<String,Int,Int>> = listOf(),
        var orderActionRecyclerPosition: Int =0
    ) : Parcelable

    @Parcelize
    data class ViewOrderFields(
        var order:Order? = null
    ) : Parcelable
}