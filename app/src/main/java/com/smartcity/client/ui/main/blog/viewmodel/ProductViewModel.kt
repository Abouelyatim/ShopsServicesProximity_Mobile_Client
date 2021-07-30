package com.smartcity.client.ui.main.blog.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.repository.main.BlogRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.deleted.*
import com.smartcity.client.ui.main.blog.state.ProductStateEvent
import com.smartcity.client.ui.main.blog.state.ProductStateEvent.*
import com.smartcity.client.ui.main.blog.state.ProductViewState
import com.smartcity.client.util.deleted.AbsentLiveData
import com.smartcity.client.util.SuccessHandling.Companion.DONE_Product_Layout_Change_Event
import javax.inject.Inject

@MainScope
class ProductViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val blogRepository: BlogRepository
): BaseViewModel<ProductStateEvent, ProductViewState>(){


    override fun handleStateEvent(stateEvent: ProductStateEvent): LiveData<DataState<ProductViewState>> {
        when(stateEvent){

            is AddProductCartEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.attemptAddProductCart(
                        authToken.account_pk!!.toLong(),
                        stateEvent.variantId,
                        stateEvent.quantity
                    )
                }?: AbsentLiveData.create()
            }
            is ProductMainEvent ->{
                //clearLayoutManagerState()
                return sessionManager.cachedToken.value?.let { authToken ->

                    blogRepository.searchBlogPosts(
                        query = "",
                        page = getPage(),
                        userId = authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is ProductSearchEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        query = getSearchQuerySearch(),
                        page = getPageSearch(),
                        userId = authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is ProductInterestEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.attemptInterestProduct(
                        page = getPageInterest(),
                        userId = authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is GetStoreCustomCategoryEvent ->{
                return blogRepository.attemptGetStoreCustomCategory(
                    stateEvent.storeId
                )
            }

            is GetProductsByCustomCategoryEvent ->{
                return blogRepository.attemptGetProductsByCustomCategory(
                    stateEvent.customCategoryId
                )
            }

            is GetAllStoreProductsEvent ->{
                return blogRepository.attemptGetAllProductsByStore(
                    stateEvent.storeId
                )
            }

            is FollowStoreEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.attemptFollowStore(
                        stateEvent.storeId,
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is StopFollowingStoreEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.attemptStopFollowingStore(
                        stateEvent.storeId,
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is IsFollowingStoreEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.attemptIsFollowingStore(
                        stateEvent.storeId,
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is ProductLayoutChangeEvent ->{
                return returnResponse(DONE_Product_Layout_Change_Event,
                    ResponseType.None())
            }

            is BackClickedEvent ->{
                return returnResponse(stateEvent.tag,
                    ResponseType.None())
            }

            is SearchEvent ->{
                return returnResponse(stateEvent.tag,
                    ResponseType.None())
            }

            is None ->{
                return liveData {
                    emit(
                        DataState<ProductViewState>(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }

    private fun returnResponse(message: String, responseType: ResponseType): LiveData<DataState<ProductViewState>>{
        Log.d(TAG, "returnErrorResponse: ${message}")

        return object: LiveData<DataState<ProductViewState>>(){
            override fun onActive() {
                super.onActive()
                value = DataState.data(null,
                    Response(
                        message,
                        responseType
                    )
                )
            }
        }
    }

    override fun initNewViewState(): ProductViewState {
        return ProductViewState()
    }


    fun cancelActiveJobs(){
        blogRepository.cancelActiveJobs() // cancel active jobs
        handlePendingData() // hide progress bar
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
        Log.d(TAG, "CLEARED...")
    }

    override fun initRepositoryViewModel() {

    }
}











