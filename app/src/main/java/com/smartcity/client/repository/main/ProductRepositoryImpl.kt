package com.smartcity.client.repository.main

import android.util.Log
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.ListProductResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.CustomCategory
import com.smartcity.client.models.product.Product
import com.smartcity.client.persistence.BlogPostDao
import com.smartcity.client.repository.safeApiCall
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.main.product.state.ProductViewState
import com.smartcity.client.ui.main.product.state.ProductViewState.ProductFields
import com.smartcity.client.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@MainScope
class ProductRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): ProductRepository
{
    private val TAG: String = "AppDebug"

    override fun attemptSearchProducts(
        stateEvent: StateEvent,
        query: String,
        page: Int,
        userId: Long
    ): Flow<DataState<ProductViewState>> = flow {
        Log.d("ii","attemptSearchProducts")


        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.searchListProduct(
                query = query,
                page = page,
                userId =userId
            )
        }
        Log.d("ii","apiResult")
        emit(
            object: ApiResponseHandler<ProductViewState, ListProductResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListProductResponse): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = ProductViewState(
                            searchProductFields = ProductViewState.SearchProductFields(
                                newSearchProductList = resultObj.results,
                                isQuerySearchInProgress = false
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptAllProducts(
        stateEvent: StateEvent,
        page: Int,
        userId: Long
    ): Flow<DataState<ProductViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.searchListProduct(
                query = "",
                page = page,
                userId =userId
            )
        }

        emit(
            object: ApiResponseHandler<ProductViewState, ListProductResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListProductResponse): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = ProductViewState(
                            productFields = ProductFields(
                                newProductList =  resultObj.results,
                                isQueryInProgress = false
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptAddProductCart(
        stateEvent: StateEvent,
        userId: Long,
        variantId: Long,
        quantity: Int
    ): Flow<DataState<ProductViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.addProductCart(
                userId,
                variantId,
                quantity
            )
        }

        emit(
            object: ApiResponseHandler<ProductViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    if(resultObj.response == SuccessHandling.SUCCESS_CREATED){
                        return DataState.data(
                            data = null,
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.DONE_ADD_TO_CART,
                                UIComponentType.Toast(),
                                MessageType.Info()
                            )
                        )
                    }else{
                        return DataState.error(
                            stateEvent = stateEvent,
                            response = Response(
                                ErrorHandling.ERROR_UNKNOWN,
                                UIComponentType.Dialog(),
                                MessageType.Error()
                            )
                        )
                    }
                }
            }.getResult()
        )
    }


    override fun attemptGetStoreCustomCategory(
        stateEvent: StateEvent,
        storeId: Long
    ): Flow<DataState<ProductViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getStoreCustomCategory(
                storeId
            )
        }

        emit(
            object: ApiResponseHandler<ProductViewState, ListGenericResponse<CustomCategory>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<CustomCategory>): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = ProductViewState(
                            viewProductFields = ProductViewState.ViewProductFields(
                                storeCustomCategoryList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Get_Store_Custom_Category,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetProductsByCustomCategory(
        stateEvent: StateEvent,
        customCategoryId: Long
    ): Flow<DataState<ProductViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getProductsByCustomCategory(
                customCategoryId
            )
        }

        emit(
            object: ApiResponseHandler<ProductViewState, ListGenericResponse<Product>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<Product>): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  ProductViewState(
                            viewProductFields = ProductViewState.ViewProductFields(
                                storeProductList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Get_Products_By_Custom_Category,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }



    override fun attemptGetAllProductsByStore(
        stateEvent: StateEvent,
        storeId: Long
    ): Flow<DataState<ProductViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getAllProductsByStore(
                storeId
            )
        }

        emit(
            object: ApiResponseHandler<ProductViewState, ListGenericResponse<Product>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<Product>): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  ProductViewState(
                            viewProductFields = ProductViewState.ViewProductFields(
                                storeProductList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Get_All_Products_By_Store,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptFollowStore(
        stateEvent: StateEvent,
        id: Long,
        idUser: Long
    ): Flow<DataState<ProductViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.followStore(
                id,
                idUser
            )
        }

        emit(
            object: ApiResponseHandler<ProductViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Follow_Store,
                            UIComponentType.Dialog(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptStopFollowingStore(
        stateEvent: StateEvent,
        id: Long,
        idUser: Long
    ): Flow<DataState<ProductViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.stopFollowingStore(
                id,
                idUser
            )
        }

        emit(
            object: ApiResponseHandler<ProductViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Stop_Following_Store,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptIsFollowingStore(
        stateEvent: StateEvent,
        id: Long,
        idUser: Long
    ): Flow<DataState<ProductViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.isFollowingStore(
                id,
                idUser
            )
        }

        emit(
            object: ApiResponseHandler<ProductViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  null,
                        stateEvent = stateEvent,
                        response = Response(
                            resultObj.response,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptInterestProduct(
        stateEvent: StateEvent,
        page: Int,
        userId: Long
    ): Flow<DataState<ProductViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getInterestProduct(
                page = page,
                userId =userId
            )
        }

        emit(
            object: ApiResponseHandler<ProductViewState, ListProductResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListProductResponse): DataState<ProductViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    Log.d(TAG,"handleSuccess ${resultObj}")
                    return DataState.data(
                        data =  ProductViewState(
                            productInterestFields = ProductViewState.ProductInterestFields(
                                newProductList =  resultObj.results,
                                isQueryInProgress = false
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }
}
















