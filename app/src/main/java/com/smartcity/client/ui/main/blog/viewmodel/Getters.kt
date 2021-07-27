package com.smartcity.client.ui.main.blog.viewmodel

import com.smartcity.client.models.BlogPost
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product

fun ProductViewModel.getPage(): Int{
    getCurrentViewStateOrNew().let {
        return it.productFields.page
    }
}


fun ProductViewModel.getIsQueryExhausted(): Boolean{
    getCurrentViewStateOrNew().let {
        return it.productFields.isQueryExhausted
    }
}

fun ProductViewModel.getIsQueryInProgress(): Boolean{
    getCurrentViewStateOrNew().let {
        return it.productFields.isQueryInProgress
    }
}

fun ProductViewModel.getProductList(): List<Product>{
    getCurrentViewStateOrNew().let {
        return it.productFields.productList
    }
}



fun ProductViewModel.getSearchQuery(): String {
    getCurrentViewStateOrNew().let {
        return it.productFields.searchQuery
    }
}

fun ProductViewModel.getViewProductFields():Product?{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.product
    }
}

fun ProductViewModel.getChoisesMap():MutableMap<String, String>{
    getCurrentViewStateOrNew().let {
        return it.choisesMap.choises
    }
}

fun ProductViewModel.getStoreCustomCategoryLists():List<CustomCategory>{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.storeCustomCategoryList
    }
}

fun ProductViewModel.getStoreProductLists():List<Product>{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.storeProductList
    }
}

fun ProductViewModel.getCustomCategoryRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.customCategoryRecyclerPosition
    }
}

fun ProductViewModel.getGridOrListView():Boolean{
    getCurrentViewStateOrNew().let {
        return it.productFields.gridOrListView
    }
}

fun ProductViewModel.getDummyBlogPost(): BlogPost{
    return BlogPost(-1, "" , "", "", "", 1, "")
}










