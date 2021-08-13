package com.smartcity.client.ui.main.account.state

import android.os.Parcelable
import com.smartcity.client.models.*
import com.smartcity.client.models.product.Category
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"

@Parcelize
class AccountViewState(
    var userInformation: UserInformation?=null,
    var addressFields:AddressFields = AddressFields(),
    var orderFields:OrderFields=OrderFields(),
    var viewOrderFields: ViewOrderFields =ViewOrderFields(),
    var aroundStoresFields: AroundStoresFields= AroundStoresFields()

) : Parcelable{

    @Parcelize
    data class AddressFields(
        var addressList:List<Address>? = null,
        var newAddress: Address? =null,
        var defaultCity :City? =null,
        var apartmentNumber:String?=null,
        var businessName:String?=null,
        var doorCodeName:String?=null
    ) : Parcelable

    @Parcelize
    data class AroundStoresFields(
        var radius:Double?=null,
        var stores:List<Store>? = null,
        var searchCity:City? =null,
        var searchRadius:Double? =null,
        var cityList: List<City>? = null,
        var cityQuery:String ?=null,
        var searchStores :List<Store>? = null,

        var categoryList:List<Category>? = null,
        var selectedCategory:String? = null
    ) : Parcelable

    @Parcelize
    data class OrderFields(
        var selectedSortFilter:Triple<String,String,String> ?=null,
        var selectedTypeFilter:Triple<String,String,String> ?=null,
        var selectedStatusFilter:Triple<String,String,String> ?=null,
        var ordersList:List<Order>? = null,
        var orderAction: List<Triple<String,Int,Int>>? = null,
        var orderActionRecyclerPosition: Int? =null
    ) : Parcelable

    @Parcelize
    data class ViewOrderFields(
        var order:Order? = null
    ) : Parcelable
}