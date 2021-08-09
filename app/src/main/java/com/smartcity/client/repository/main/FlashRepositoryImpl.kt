package com.smartcity.client.repository.main

import android.util.Log
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.ListGenericDto
import com.smartcity.client.api.interest.dto.CityDto
import com.smartcity.client.api.main.OpenApiMainService
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.City
import com.smartcity.client.models.FlashDeal
import com.smartcity.client.models.product.Product
import com.smartcity.client.repository.safeApiCall
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.interest.state.ConfigurationFields
import com.smartcity.client.ui.interest.state.InterestViewState
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@MainScope
class FlashRepositoryImpl
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val sessionManager: SessionManager
): FlashRepository
{
    private val TAG: String = "AppDebug"

    override fun attemptUserFlashDeals(
        stateEvent: StateEvent,
        id: Long,
        date :String
    ): Flow<DataState<FlashViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getUserFlashDeals(
                id = id,
                date = date
            )
        }

        emit(
            object: ApiResponseHandler<FlashViewState,ListGenericResponse<FlashDeal>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<FlashDeal>): DataState<FlashViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = FlashViewState(
                            flashFields = FlashViewState.FlashFields(
                                networkFlashDealsPair = Pair(date,resultObj.results)
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptUserDiscountProduct(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<FlashViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getUserDiscountProduct(
                id = id
            )
        }

        emit(
            object: ApiResponseHandler<FlashViewState,ListGenericResponse<Product>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<Product>): DataState<FlashViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = FlashViewState(
                            flashFields = FlashViewState.FlashFields(
                                productDiscountList = resultObj.results
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptSearchFlashDeals(
        stateEvent: StateEvent,
        latitude: Double,
        longitude: Double,
        date: String
    ): Flow<DataState<FlashViewState>>  = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.searchUserFlashDeals(
                latitude = latitude,
                longitude = longitude,
                date = date
            )
        }

        emit(
            object: ApiResponseHandler<FlashViewState,ListGenericResponse<FlashDeal>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<FlashDeal>): DataState<FlashViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = FlashViewState(
                            flashFields = FlashViewState.FlashFields(
                                searchNetworkFlashDealsPair = Pair(date,resultObj.results)
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }
            }.getResult()
        )
    }

    override fun attemptSearchDiscountProduct(
        stateEvent: StateEvent,
        latitude: Double,
        longitude: Double
    ): Flow<DataState<FlashViewState>>  = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.searchUserDiscountProduct(
                latitude = latitude,
                longitude = longitude
            )
        }

        emit(
            object: ApiResponseHandler<FlashViewState,ListGenericResponse<Product>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericResponse<Product>): DataState<FlashViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")

                    return DataState.data(
                        data = FlashViewState(
                            flashFields = FlashViewState.FlashFields(
                                searchProductDiscountList = resultObj.results
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
    ): Flow<DataState<FlashViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.addProductCart(
                userId,
                variantId,
                quantity
            )
        }

        emit(
            object: ApiResponseHandler<FlashViewState,GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<FlashViewState> {
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

    override fun attemptUserDefaultCity(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<FlashViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.getDefaultCity(
                id = id
            )
        }
        emit(
            object: ApiResponseHandler<FlashViewState, City>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: City): DataState<FlashViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = FlashViewState(
                            flashFields = FlashViewState.FlashFields(
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

    override fun attemptResolveUserAddress(
        stateEvent: StateEvent,
        country: String,
        city: String
    ): Flow<DataState<FlashViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiMainService.resolveUserCity(
                country = country,
                city = city
            )
        }
        emit(
            object: ApiResponseHandler<FlashViewState, ListGenericDto<City, CityDto>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericDto<City, CityDto>): DataState<FlashViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    Log.d(TAG,"handleSuccess result ${resultObj}")
                    val cityList = resultObj.toList()
                    return DataState.data(
                        data = FlashViewState(
                            flashFields = FlashViewState.FlashFields(
                                cityList = cityList
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Resolve_User_Address,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }

            }.getResult()
        )
    }
}