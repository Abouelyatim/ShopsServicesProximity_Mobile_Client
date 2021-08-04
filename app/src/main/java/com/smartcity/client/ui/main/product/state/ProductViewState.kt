package com.smartcity.client.ui.main.product.state

import android.os.Parcelable
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import kotlinx.android.parcel.Parcelize

const val BLOG_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState"

@Parcelize
data class ProductViewState (

    var productFields: ProductFields = ProductFields(),

    var productInterestFields: ProductInterestFields = ProductInterestFields(),

    var searchProductFields: SearchProductFields = SearchProductFields(),

    var viewProductFields: ViewProductFields = ViewProductFields(),

    var choisesMap: ChoisesMap = ChoisesMap()

    ): Parcelable {

    @Parcelize
    data class ViewProductFields(
        var product: Product? = null,
        var storeCustomCategoryList: List<CustomCategory> ? = null,
        var customCategoryRecyclerPosition:Int? = null,
        var storeProductList:List<Product> ? = null
    ) : Parcelable

    @Parcelize
    data class ChoisesMap(
        var choises:MutableMap<String, String> ? = null
    ) : Parcelable

    @Parcelize
    data class ProductFields(
        var newProductList: List<Product> ? = null,
        var productList: List<Product> ? = null,
        var page: Int ? = null,
        var isQueryInProgress: Boolean ? = null,
        var isQueryExhausted: Boolean ? = null,
        var gridOrListView:Boolean ? = null
    ) : Parcelable

    @Parcelize
    data class SearchProductFields(
        var newSearchProductList: List<Product> ? = null,
        var productSearchList: List<Product> ? = null,
        var searchQuery: String ? = null,
        var pageSearch: Int ? = null,
        var isQuerySearchInProgress: Boolean ? = null,
        var isQuerySearchExhausted: Boolean ? = null,
        var gridOrListSearchView:Boolean ? = null
    ) : Parcelable

    @Parcelize
    data class ProductInterestFields(
        var newProductList: List<Product> ? = null,
        var productList: List<Product> ? = null,
        var page: Int ? = null,
        var isQueryInProgress: Boolean ? = null,
        var isQueryExhausted: Boolean ? = null
    ) : Parcelable
}








