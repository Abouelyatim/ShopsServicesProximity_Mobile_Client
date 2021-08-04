package com.smartcity.client.ui.main.account.state

import android.os.Parcelable
import com.smartcity.client.models.Address
import com.smartcity.client.models.Order
import com.smartcity.client.models.Store
import com.smartcity.client.models.UserInformation
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"

@Parcelize
class AccountViewState(
    var userInformation: UserInformation?=null,
    var addressList:List<Address>? = null,
    var orderFields:OrderFields=OrderFields(),
    var viewOrderFields: ViewOrderFields =ViewOrderFields(),
    var aroundStoresFields: AroundStoresFields= AroundStoresFields()

) : Parcelable{

    @Parcelize
    data class AroundStoresFields(
        var centerLatitude:Double?=null,
        var centerLongitude:Double?=null,
        var radius:Double?=null,
        var stores:List<Store>? = null
    ) : Parcelable

    @Parcelize
    data class OrderFields(
        var ordersList:List<Order>? = null,
        var orderAction: List<Triple<String,Int,Int>>? = null,
        var orderActionRecyclerPosition: Int? =null
    ) : Parcelable

    @Parcelize
    data class ViewOrderFields(
        var order:Order? = null
    ) : Parcelable
}