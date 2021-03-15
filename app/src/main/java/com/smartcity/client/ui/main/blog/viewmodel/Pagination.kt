import android.util.Log
import com.smartcity.client.ui.main.blog.state.ProductStateEvent
import com.smartcity.client.ui.main.blog.state.ProductStateEvent.*
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.ui.main.blog.viewmodel.*

fun ProductViewModel.loadProductMainList(){
    setQueryInProgress(true)
    setQueryExhausted(false)
    setStateEvent(ProductMainEvent())
}


fun ProductViewModel.handleIncomingBlogListData(viewState: ProductViewState){
    Log.d(TAG, "BlogViewModel, DataState: ${viewState}")
    Log.d(TAG, "BlogViewModel, DataState: isQueryInProgress?: " +
            "${viewState.productFields.isQueryInProgress}")
    Log.d(TAG, "BlogViewModel, DataState: isQueryExhausted?: " +
            "${viewState.productFields.isQueryExhausted}")
    setQueryInProgress(viewState.productFields.isQueryInProgress)
    setQueryExhausted(viewState.productFields.isQueryExhausted)


    val list=getProductList().toMutableList()
    viewState.productFields.newProductList.map {
        list.add(it)
    }
    if(viewState.productFields.newProductList.isEmpty()){
        setQueryExhausted(true)
    }
    val listDistinct= list.distinct()
    setProductListData(listDistinct)
}

fun ProductViewModel.nextPage(){
    if(!getIsQueryExhausted()){
       incrementPageNumber()
        setQueryInProgress(true)
        setStateEvent(ProductMainEvent())
    }
}

 fun ProductViewModel.incrementPageNumber(){
    val update = getCurrentViewStateOrNew()
    val page = update.copy().productFields.page // get current page
    update.productFields.page = page + 1
    setViewState(update)
}


fun ProductViewModel.resetPage(){
    val update = getCurrentViewStateOrNew()
    update.productFields.page = 1
    setViewState(update)
}



fun ProductViewModel.loadFirstPage() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    if(getSearchQuery()!=""){
        clearQuery()
    }
    clearProductListData()
    setStateEvent(ProductMainEvent())
    Log.e(TAG, "BlogViewModel: loadFirstPage: ${viewState.value!!.productFields.searchQuery}")
}

fun ProductViewModel.loadFirstSearchPage() {
    setQueryInProgress(true)
    setQueryExhausted(false)
    resetPage()
    clearProductListData()
    setStateEvent(ProductMainEvent())
    Log.e(TAG, "BlogViewModel: loadFirstPage: ${viewState.value!!.productFields.searchQuery}")
}






