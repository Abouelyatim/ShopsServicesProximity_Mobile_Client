package com.smartcity.client.repository.main

import android.util.Log
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.api.main.responses.ListAddressResponse
import com.smartcity.client.api.main.responses.ListOrderResponse
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.City
import com.smartcity.client.models.Store
import com.smartcity.client.models.UserInformation
import com.smartcity.client.persistence.AccountPropertiesDao
import com.smartcity.client.repository.buildError
import com.smartcity.client.repository.safeApiCall
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@MainScope
class AccountRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
): AccountRepository
{
    private val TAG: String = "AppDebug"

    override fun attemptCreateAddress(
        stateEvent: StateEvent,
        address: Address
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.createAddress(
                address = address
            )
        }
        emit(
            object: ApiResponseHandler<AccountViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
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

    override fun attemptUserAddresses(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getUserAddresses(
                userId = id
            )
        }
        emit(
            object: ApiResponseHandler<AccountViewState, ListAddressResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListAddressResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = AccountViewState(
                            addressFields = AccountViewState.AddressFields(
                                addressList=resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptDeleteAddress(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.deleteUserAddress(
                id=id
            )
        }
        emit(
            object: ApiResponseHandler<AccountViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    if(resultObj.response == SuccessHandling.DELETE_DONE){
                        return DataState.data(
                            data = null,
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

    override fun attemptSetUserInformation(
        stateEvent: StateEvent,
        userInformation: UserInformation
    ): Flow<DataState<AccountViewState>> = flow {

        val createUserInformationError=userInformation.isValidForCreation()
        if(!createUserInformationError.equals(UserInformation.CreateUserInformationError.none())){
            emit(
                buildError<AccountViewState>(
                    createUserInformationError,
                    UIComponentType.Dialog(),
                    stateEvent
                )
            )
        }

        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.setUserInformation(
                userInformation = userInformation
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
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
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getUserInformation(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, UserInformation>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: UserInformation): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  AccountViewState(
                            userInformation=resultObj
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

    override fun attemptUserInProgressOrders(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getUserInProgressOrders(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, ListOrderResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListOrderResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  AccountViewState(
                            orderFields= AccountViewState.OrderFields(
                                ordersList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_USER_ORDERS,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }


    override fun attemptUserFinalizedOrders(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getUserFinalizedOrders(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, ListOrderResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListOrderResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  AccountViewState(
                            orderFields= AccountViewState.OrderFields(
                                ordersList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_USER_ORDERS,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }



    override fun attemptConfirmOrderReceived(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.confirmOrderReceived(
                id= id
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =  null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.CUSTOM_CATEGORY_UPDATE_DONE,
                            UIComponentType.Toast(),
                            MessageType.Info()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptGetStoreAround(
        stateEvent: StateEvent,
        centerLatitude: Double,
        centerLongitude: Double,
        radius: Double
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getStoresAround(
                distance = radius,
                latitude = centerLatitude,
                longitude = centerLongitude
            )
        }

        emit(
            object: ApiResponseHandler<AccountViewState, ListGenericResponse<Store>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<Store>): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data =   AccountViewState(
                            aroundStoresFields = AccountViewState.AroundStoresFields(
                                stores = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_STORE_AROUND,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }

    override fun attemptUserDefaultCity(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getDefaultCity(
                id = id
            )
        }
        emit(
            object: ApiResponseHandler<AccountViewState, City>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: City): DataState<AccountViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = AccountViewState(
                            addressFields = AccountViewState.AddressFields(
                                defaultCity= resultObj
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            stateEvent.toString(),
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }
            }.getResult()
        )
    }
}












