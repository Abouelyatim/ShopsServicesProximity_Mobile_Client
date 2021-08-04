package com.smartcity.client.ui.main.product.viewmodel

import android.util.Log
import com.smartcity.client.ui.main.product.state.ProductStateEvent
import com.smartcity.client.ui.main.product.state.ProductStateEvent.ProductMainEvent
import com.smartcity.client.ui.main.product.state.ProductViewState

fun ProductViewModel.loadProductMainList(){
    setQueryInProgress(true)
    setQueryExhausted(false)
    setStateEvent(ProductMainEvent())
}

fun ProductViewModel.loadProductInterestList(){
    Log.d("ii","loadProductInterestList")
    setQueryInProgressInterest(true)
    setQueryExhaustedInterest(false)
    setStateEvent(ProductStateEvent.ProductInterestEvent())
}

fun ProductViewModel.handleIncomingProductListData(viewState: ProductViewState){
    val list=getProductList().toMutableList()
    viewState.productFields.newProductList?.let { newProductList ->
        list.addAll(newProductList)

        if(newProductList.isEmpty()){
            setQueryExhausted(true)
        }
        val listDistinct= list.distinct()
        setProductListData(listDistinct)
    }
}

fun ProductViewModel.handleIncomingProductSearchListData(viewState: ProductViewState){
    val list=getProductListSearch().toMutableList()
    viewState.searchProductFields.newSearchProductList?.let {newProductList ->
        list.addAll(newProductList)

        if(newProductList.isEmpty()){
            setQueryExhaustedSearch(true)
        }

        val listDistinct= list.distinct()
        setProductListDataSearch(listDistinct)
    }
}

fun ProductViewModel.handleIncomingProductInterestListData(viewState: ProductViewState){
    val list=getProductListInterest().toMutableList()
    viewState.productInterestFields.newProductList?.let { newProductList ->
        list.addAll(newProductList)

        if(newProductList.isEmpty()){
            setQueryExhaustedInterest(true)
        }

        val listDistinct= list.distinct()
        setProductListDataInterest(listDistinct)
    }
}

fun ProductViewModel.nextPage(){
    if(!getIsQueryExhausted() && !getIsQueryInProgress()){
       incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(ProductMainEvent())
    }
}

fun ProductViewModel.nextPageSearch(){
    if(!getIsQueryExhaustedSearch() && !getIsQueryInProgressSearch()){
        incrementPageNumberSearch()
        setQueryInProgressSearch(true)
        setStateEvent(ProductMainEvent())
    }
}

fun ProductViewModel.nextPageInterest(){
    if(!getIsQueryExhaustedInterest() && !getIsQueryInProgressInterest()){
        incrementPageNumberInterest()
        setQueryInProgressInterest(true)
        setStateEvent(ProductStateEvent.ProductInterestEvent())
    }
}

 fun ProductViewModel.incrementPageNumber(){
    val update = getCurrentViewStateOrNew()
    val page = update.copy().productFields.page?: 1 // get current page
    update.productFields.page = page + 1
    setViewState(update)
}

fun ProductViewModel.incrementPageNumberSearch(){
    val update = getCurrentViewStateOrNew()
    val page = update.copy().searchProductFields.pageSearch?: 1 // get current page
    update.searchProductFields.pageSearch = page + 1
    setViewState(update)
}

fun ProductViewModel.incrementPageNumberInterest(){
    val update = getCurrentViewStateOrNew()
    val page = update.copy().productInterestFields.page?: 1 // get current page
    update.productInterestFields.page = page + 1
    setViewState(update)
}

fun ProductViewModel.resetPage(){
    val update = getCurrentViewStateOrNew()
    update.productFields.page = 1
    setViewState(update)
}

fun ProductViewModel.resetPageSearch(){
    val update = getCurrentViewStateOrNew()
    update.searchProductFields.pageSearch = 1
    setViewState(update)
}

fun ProductViewModel.resetPageInterest(){
    val update = getCurrentViewStateOrNew()
    update.productInterestFields.page = 1
    setViewState(update)
}

fun ProductViewModel.loadFirstPage() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    clearProductListData()
    setStateEvent(ProductMainEvent())
}

fun ProductViewModel.loadFirstPageInterest() {
    setQueryInProgressInterest(true)
    setQueryExhaustedInterest(false)
    resetPageInterest()
    clearProductListDataInterest()
    setStateEvent(ProductStateEvent.ProductInterestEvent())
}

fun ProductViewModel.loadFirstSearchPageSearch() {
    setQueryInProgressSearch(true)
    setQueryExhaustedSearch(false)
    resetPageSearch()
    clearProductListDataSearch()
    setStateEvent(ProductStateEvent.ProductSearchEvent())
}







