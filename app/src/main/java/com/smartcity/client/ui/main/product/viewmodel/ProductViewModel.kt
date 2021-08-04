package com.smartcity.client.ui.main.product.viewmodel

import android.util.Log
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.repository.main.ProductRepositoryImpl
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.main.product.state.ProductStateEvent.*
import com.smartcity.client.ui.main.product.state.ProductViewState
import com.smartcity.client.util.*
import com.smartcity.client.util.SuccessHandling.Companion.DONE_Product_Layout_Change_Event
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class ProductViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val productRepository: ProductRepositoryImpl
): BaseViewModel<ProductViewState>(){

    override fun handleNewData(data: ProductViewState) {
        data.searchProductFields.let { searchProductFields ->
            searchProductFields.newSearchProductList?.let {list ->

                searchProductFields.isQuerySearchInProgress?.let {isQueryInProgress ->
                    setQueryInProgressSearch(isQueryInProgress)
                }

                searchProductFields.isQuerySearchExhausted?.let {isQueryExhausted ->
                    setQueryExhaustedSearch(isQueryExhausted)
                }

                handleIncomingProductSearchListData(data)
            }
        }

        data.productFields.let { productFields ->
            productFields.newProductList?.let {list ->

                productFields.isQueryInProgress?.let {isQueryInProgress ->
                    setQueryInProgress(isQueryInProgress)
                }

                productFields.isQueryExhausted?.let {isQueryExhausted ->
                    setQueryExhausted(isQueryExhausted)
                }

                handleIncomingProductListData(data)
            }
        }

        data.productInterestFields.let { productInterestFields ->
            productInterestFields.newProductList?.let {list ->

                productInterestFields.isQueryInProgress?.let {isQueryInProgress ->
                    setQueryInProgressInterest(isQueryInProgress)
                }

                productInterestFields.isQueryExhausted?.let {isQueryExhausted ->
                    setQueryExhaustedInterest(isQueryExhausted)
                }

                handleIncomingProductInterestListData(data)
            }
        }

        data.viewProductFields.let {viewProductFields ->
            viewProductFields.storeCustomCategoryList?.let {list ->
                setStoreCustomCategoryLists(list)
            }

            viewProductFields.storeProductList?.let { list ->
                setStoreProductLists(list)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)){
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<ProductViewState>> = when(stateEvent){

                    is AddProductCartEvent ->{
                        productRepository.attemptAddProductCart(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.variantId,
                            stateEvent.quantity
                        )
                    }

                    is ProductMainEvent ->{
                        productRepository.attemptAllProducts(
                            stateEvent,
                            page = getPage(),
                            userId = authToken.account_pk!!.toLong()
                        )
                    }

                    is ProductSearchEvent ->{
                        productRepository.attemptSearchProducts(
                            stateEvent,
                            query = getSearchQuerySearch(),
                            page = getPageSearch(),
                            userId = authToken.account_pk!!.toLong()
                        )
                    }

                    is ProductInterestEvent ->{
                        productRepository.attemptInterestProduct(
                            stateEvent,
                            page = getPageInterest(),
                            userId = authToken.account_pk!!.toLong()
                        )
                    }

                    is GetStoreCustomCategoryEvent ->{
                         productRepository.attemptGetStoreCustomCategory(
                            stateEvent,
                            stateEvent.storeId
                        )
                    }

                    is GetProductsByCustomCategoryEvent ->{
                         productRepository.attemptGetProductsByCustomCategory(
                            stateEvent,
                            stateEvent.customCategoryId
                        )
                    }

                    is GetAllStoreProductsEvent ->{
                         productRepository.attemptGetAllProductsByStore(
                            stateEvent,
                            stateEvent.storeId
                        )
                    }

                    is FollowStoreEvent ->{
                        productRepository.attemptFollowStore(
                            stateEvent,
                            stateEvent.storeId,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is StopFollowingStoreEvent ->{
                        productRepository.attemptStopFollowingStore(
                            stateEvent,
                            stateEvent.storeId,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is IsFollowingStoreEvent ->{
                        productRepository.attemptIsFollowingStore(
                            stateEvent,
                            stateEvent.storeId,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    else -> {
                        flow{
                            emit(
                                DataState.error<ProductViewState>(
                                    response = Response(
                                        message = ErrorHandling.INVALID_STATE_EVENT,
                                        uiComponentType = UIComponentType.None(),
                                        messageType = MessageType.Error()
                                    ),
                                    stateEvent = stateEvent
                                )
                            )
                        }
                    }
                }
                launchJob(stateEvent, job)
            }
        }
    }

    override fun initNewViewState(): ProductViewState {
        return ProductViewState()
    }
}











