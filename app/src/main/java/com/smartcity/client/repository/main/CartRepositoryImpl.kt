package com.smartcity.client.repository.main

import android.util.Log
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.ListAddressResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.BillTotal
import com.smartcity.client.models.Order
import com.smartcity.client.models.UserInformation
import com.smartcity.client.models.product.Cart
import com.smartcity.client.persistence.BlogPostDao
import com.smartcity.client.repository.safeApiCall
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.ui.main.cart.state.CartViewState.CartFields
import com.smartcity.client.ui.main.cart.state.CartViewState.OrderFields
import com.smartcity.client.util.*
import com.smartcity.provider.models.Policy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@MainScope
class CartRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): CartRepository
{
    private val TAG: String = "AppDebug"

    override fun attemptUserCart(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CartViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getUserCart(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<CartViewState, Cart>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: Cart): DataState<CartViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  CartViewState(
                            cartFields = CartFields(
                                cartList = resultObj
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_USER_CART,
                            UIComponentType.None(),
                            MessageType.None()
                        )
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
    ): Flow<DataState<CartViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.addProductCart(
                userId,
                variantId,
                quantity
            )
        }

        emit(
            object: ApiResponseHandler<CartViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<CartViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    if(resultObj.response == SuccessHandling.SUCCESS_CREATED){
                        return DataState.data(
                            data =  null,
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.DONE_UPDATE_CART_QUANTITY,
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

    override fun attemptDeleteProductCart(
        stateEvent: StateEvent,
        userId: Long,
        variantId: Long
    ): Flow<DataState<CartViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.deleteProductCart(
                userId,
                variantId
            )
        }

        emit(
            object: ApiResponseHandler<CartViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<CartViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    if(resultObj.response == SuccessHandling.DELETE_DONE){
                        return DataState.data(
                            data =  null,
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.DELETE_DONE,
                                UIComponentType.SnackBar(),
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

    override fun attemptPlaceOrder(
        stateEvent: StateEvent,
        order: Order
    ): Flow<DataState<CartViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.placeOrder(
                order
            )
        }

        emit(
            object: ApiResponseHandler<CartViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<CartViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    if(resultObj.response == SuccessHandling.SUCCESS_CREATED){
                        return DataState.data(
                            data =  null,
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.DONE_PLACE_ORDER,
                                UIComponentType.SnackBar(),
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

    override fun attemptStorePolicy(
        stateEvent: StateEvent,
        storeId: Long
    ): Flow<DataState<CartViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getStorePolicy(
                storeId = storeId
            )
        }

        emit(
            object: ApiResponseHandler<CartViewState, Policy>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: Policy): DataState<CartViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  CartViewState(
                            orderFields = OrderFields(
                                storePolicy = resultObj
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_STORE_POLICY,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptTotalBill(
        stateEvent: StateEvent,
        bill: BillTotal
    ): Flow<DataState<CartViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getTotalBill(
                bill = bill
            )
        }

        emit(
            object: ApiResponseHandler<CartViewState, BillTotal>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: BillTotal): DataState<CartViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  CartViewState(
                            orderFields = OrderFields(
                                total = resultObj
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_TOTAL_BILL,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptUserAddresses(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CartViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getUserAddresses(
                userId = id
            )
        }

        emit(
            object: ApiResponseHandler<CartViewState, ListAddressResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListAddressResponse): DataState<CartViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  CartViewState(
                            orderFields=OrderFields(
                                addressList=resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_USER_ADDRESSES,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }


    override fun attemptCreateAddress(
        stateEvent: StateEvent,
        address: Address
    ): Flow<DataState<CartViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.createAddress(
                address = address
            )
        }

        emit(
            object: ApiResponseHandler<CartViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<CartViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CREATED_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetUserInformation(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CartViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getUserInformation(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<CartViewState, UserInformation>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: UserInformation): DataState<CartViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = CartViewState(
                            orderFields = OrderFields(
                                userInformation = resultObj
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_USER_INFORMATION,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }
}
















