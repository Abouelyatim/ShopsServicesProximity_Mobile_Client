package com.smartcity.client.ui.main.account.state

import android.os.Parcelable
import com.smartcity.client.models.Address
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"

@Parcelize
class AccountViewState(

    var addressList:List<Address> = listOf()

) : Parcelable{



}