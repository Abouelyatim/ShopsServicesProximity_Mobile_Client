package com.smartcity.client.ui.main.product.viewmodel

import com.smartcity.client.models.BlogPost
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product

fun ProductViewModel.getPage(): Int{
    getCurrentViewStateOrNew().let {
        return it.productFields.page?:1
    }
}

fun ProductViewModel.getIsQueryExhausted(): Boolean{
    getCurrentViewStateOrNew().let {
        return it.productFields.isQueryExhausted?:false
    }
}

fun ProductViewModel.getIsQueryInProgress(): Boolean{
    getCurrentViewStateOrNew().let {
        return it.productFields.isQueryInProgress?:false
    }
}

fun ProductViewModel.getProductList(): List<Product>{
    getCurrentViewStateOrNew().let {
        return it.productFields.productList?:ArrayList<Product>()
    }
}

fun ProductViewModel.getViewProductFields():Product?{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.product
    }
}

fun ProductViewModel.getChoisesMap():MutableMap<String, String>{
    getCurrentViewStateOrNew().let {
        return it.choisesMap.choises?: mutableMapOf()
    }
}

fun ProductViewModel.getStoreCustomCategoryLists():List<CustomCategory>{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.storeCustomCategoryList?:ArrayList<CustomCategory>()
    }
}

fun ProductViewModel.getStoreProductLists():List<Product>{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.storeProductList?: ArrayList()
    }
}

fun ProductViewModel.getCustomCategoryRecyclerPosition():Int{
    getCurrentViewStateOrNew().let {
        return it.viewProductFields.customCategoryRecyclerPosition?:0
    }
}

fun ProductViewModel.getGridOrListView():Boolean{
    getCurrentViewStateOrNew().let {
        return it.productFields.gridOrListView?:false
    }
}

fun ProductViewModel.getPageSearch(): Int{
    getCurrentViewStateOrNew().let {
        return it.searchProductFields.pageSearch?:1
    }
}

fun ProductViewModel.getIsQueryExhaustedSearch(): Boolean{
    getCurrentViewStateOrNew().let {
        return it.searchProductFields.isQuerySearchExhausted?:false
    }
}

fun ProductViewModel.getIsQueryInProgressSearch(): Boolean{
    getCurrentViewStateOrNew().let {
        return it.searchProductFields.isQuerySearchInProgress?:false
    }
}

fun ProductViewModel.getProductListSearch(): List<Product>{
    getCurrentViewStateOrNew().let {
        return it.searchProductFields.productSearchList?:ArrayList<Product>()
    }
}

fun ProductViewModel.getSearchQuerySearch(): String {
    getCurrentViewStateOrNew().let {
        return it.searchProductFields.searchQuery?:""
    }
}

fun ProductViewModel.getGridOrListViewSearch():Boolean{
    getCurrentViewStateOrNew().let {
        return it.searchProductFields.gridOrListSearchView?:false
    }
}

fun ProductViewModel.getPageInterest(): Int{
    getCurrentViewStateOrNew().let {
        return it.productInterestFields.page?:1
    }
}

fun ProductViewModel.getIsQueryExhaustedInterest(): Boolean{
    getCurrentViewStateOrNew().let {
        return it.productInterestFields.isQueryExhausted?:false
    }
}

fun ProductViewModel.getIsQueryInProgressInterest(): Boolean{
    getCurrentViewStateOrNew().let {
        return it.productInterestFields.isQueryInProgress?:false
    }
}

fun ProductViewModel.getProductListInterest(): List<Product>{
    getCurrentViewStateOrNew().let {
        return it.productInterestFields.productList?:ArrayList<Product>()
    }
}