package com.smartcity.client.ui.main.store.state

import android.os.Parcelable
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import kotlinx.android.parcel.Parcelize

const val ACCOUNT_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.account.state.AccountViewState"

@Parcelize
class StoreViewState(

    var viewCustomCategoryFields: ViewCustomCategoryFields = ViewCustomCategoryFields(),

    var viewProductList: ViewProductList = ViewProductList(),

    var viewProductFields: ViewProductFields = ViewProductFields(),

    var choisesMap: ChoisesMap = ChoisesMap(),

    var customCategoryRecyclerPosition:Int=0

    ) : Parcelable{

    @Parcelize
    data class ViewProductList(
        var products:List<Product> = ArrayList()
    ) : Parcelable

    @Parcelize
    data class ViewCustomCategoryFields(
        var customCategoryList: List<CustomCategory> = ArrayList<CustomCategory>()
    ) : Parcelable

    @Parcelize
    data class ViewProductFields(
        var product: Product? = null
    ) : Parcelable

    @Parcelize
    data class ChoisesMap(
        var choises:MutableMap<String, String> = mutableMapOf()
    ) : Parcelable
}