package com.smartcity.client.ui.main.blog.state

import android.net.Uri
import android.os.Parcelable
import com.smartcity.client.models.BlogPost
import com.smartcity.client.models.product.Product
import com.smartcity.client.persistence.BlogQueryUtils.Companion.BLOG_ORDER_ASC
import com.smartcity.client.persistence.BlogQueryUtils.Companion.ORDER_BY_ASC_DATE_UPDATED
import com.smartcity.client.ui.main.store.state.StoreViewState
import kotlinx.android.parcel.Parcelize

const val BLOG_VIEW_STATE_BUNDLE_KEY = "com.codingwithmitch.openapi.ui.main.blog.state.BlogViewState"

@Parcelize
data class ProductViewState (

    // BlogFragment vars
    var productFields: ProductFields = ProductFields(),

    var viewProductFields: ViewProductFields = ViewProductFields(),

    var choisesMap: ChoisesMap = ChoisesMap()

    ): Parcelable {

    @Parcelize
    data class ViewProductFields(
        var product: Product? = null
    ) : Parcelable

    @Parcelize
    data class ChoisesMap(
        var choises:MutableMap<String, String> = mutableMapOf()
    ) : Parcelable

    @Parcelize
    data class ProductFields(
        var newProductList: List<Product> = ArrayList<Product>(),
        var productList: List<Product> = ArrayList<Product>(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var layoutManagerState: Parcelable? = null
    ) : Parcelable



}








