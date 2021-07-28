package com.smartcity.client.ui.main.blog.state

import android.os.Parcelable
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import kotlinx.android.parcel.Parcelize

const val BLOG_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState"

@Parcelize
data class ProductViewState (

    // BlogFragment vars
    var productFields: ProductFields = ProductFields(),

    var searchProductFields: SearchProductFields = SearchProductFields(),

    var viewProductFields: ViewProductFields = ViewProductFields(),

    var choisesMap: ChoisesMap = ChoisesMap()

    ): Parcelable {

    @Parcelize
    data class ViewProductFields(
        var product: Product? = null,
        var storeCustomCategoryList: List<CustomCategory> = ArrayList<CustomCategory>(),
        var customCategoryRecyclerPosition:Int=0,
        var storeProductList:List<Product> = ArrayList()
    ) : Parcelable

    @Parcelize
    data class ChoisesMap(
        var choises:MutableMap<String, String> = mutableMapOf()
    ) : Parcelable

    @Parcelize
    data class ProductFields(
        var newProductList: List<Product> = ArrayList<Product>(),
        var productList: List<Product> = ArrayList<Product>(),
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var gridOrListView:Boolean =false
    ) : Parcelable

    @Parcelize
    data class SearchProductFields(
        var newSearchProductList: List<Product> = ArrayList<Product>(),
        var productSearchList: List<Product> = ArrayList<Product>(),
        var searchQuery: String = "",
        var pageSearch: Int = 1,
        var isQuerySearchInProgress: Boolean = false,
        var isQuerySearchExhausted: Boolean = false,
        var gridOrListSearchView:Boolean =false
    ) : Parcelable

}








