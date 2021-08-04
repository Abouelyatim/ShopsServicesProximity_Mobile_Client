package com.smartcity.client.repository.interest

import android.content.SharedPreferences
import android.util.Log
import com.smartcity.client.api.GenericResponse
import com.smartcity.client.api.ListGenericDto
import com.smartcity.client.api.interest.*
import com.smartcity.client.api.interest.dto.CategoryDto
import com.smartcity.client.api.interest.dto.CityDto
import com.smartcity.client.di.interest.InterestScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.AuthToken
import com.smartcity.client.models.City
import com.smartcity.client.models.product.Category
import com.smartcity.client.persistence.AuthTokenDao
import com.smartcity.client.repository.buildError
import com.smartcity.client.repository.safeApiCall
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.interest.state.CategoryFields
import com.smartcity.client.ui.interest.state.ConfigurationFields
import com.smartcity.client.ui.interest.state.InterestViewState
import com.smartcity.client.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@InterestScope
class InterestRepositoryImpl
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val openApiInterestService: OpenApiInterestService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
): InterestRepository
{
    private val TAG: String = "AppDebug"

    override fun attemptAllCategory(
        stateEvent: StateEvent
    ): Flow<DataState<InterestViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiInterestService.getAllCategory()
        }

        emit(
            object: ApiResponseHandler<InterestViewState, ListGenericDto<Category, CategoryDto>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericDto<Category, CategoryDto>): DataState<InterestViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    val categoryList=resultObj.toList()
                    return DataState.data(
                        data = InterestViewState(
                            categoryFields = CategoryFields(
                                categoryList = categoryList
                            )
                        ),
                        stateEvent = stateEvent,
                        response = null
                    )
                }

            }.getResult()
        )
    }

    override fun attemptSetInterestCenter(
        stateEvent: StateEvent,
        id: Long,
        interest: List<String>
    ): Flow<DataState<InterestViewState>> = flow {
        val selectedCategoriesError = CategoryFields(
            listOf(),
            listOf(),
            interest.toMutableList()
        ).isValid()

        if(!selectedCategoriesError.equals(CategoryFields.SelectedCategoriesError.none())){
            emit(
                buildError<InterestViewState>(
                    selectedCategoriesError,
                    UIComponentType.Dialog(),
                    stateEvent
                )
            )
        }

        val apiResult = safeApiCall(Dispatchers.IO){
            openApiInterestService.setInterestCenter(
                id,
                interest
            )
        }

        emit(
            object: ApiResponseHandler<InterestViewState, GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<InterestViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    if (resultObj.response == SuccessHandling.CREATED_DONE){
                        // will return -1 if failure
                        val result = authTokenDao.insert(
                            AuthToken(
                                sessionManager.cachedToken.value!!.account_pk,
                                sessionManager.cachedToken.value!!.token,
                                true
                            )
                        )
                        if(result < 0){
                            return DataState.error(
                                response = Response(
                                    ErrorHandling.ERROR_SAVE_AUTH_TOKEN,
                                    UIComponentType.Dialog(),
                                    MessageType.Error()
                                ),
                                stateEvent = stateEvent
                            )
                        }

                        sharedPrefsEditor.putStringSet(PreferenceKeys.USER_INTEREST_CENTER,interest.toMutableSet())
                        sharedPrefsEditor.apply()

                        return DataState.data(
                            data = null,
                            stateEvent = stateEvent,
                            response = Response(
                                SuccessHandling.CREATED_DONE,
                                UIComponentType.None(),
                                MessageType.None()
                            )
                        )
                    }else{
                        return DataState.error(
                            response = Response(
                                ErrorHandling.GENERIC_AUTH_ERROR,
                                UIComponentType.Dialog(),
                                MessageType.Error()
                            ),
                            stateEvent = stateEvent
                        )
                    }
                }
            }.getResult()
        )
    }

    override fun attemptUserInterestCenter(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<InterestViewState>> = flow {
        val interestCenter=sharedPreferences.getStringSet(PreferenceKeys.USER_INTEREST_CENTER, null)
        if (!interestCenter.isNullOrEmpty()){//if interest is set so do not do request
            emit(
                buildError<InterestViewState>(
                    "",
                    UIComponentType.None(),
                    stateEvent
                )
            )
        }

        val apiResult = safeApiCall(Dispatchers.IO){
            openApiInterestService.getUserInterestCenter(
                id
            )
        }
        emit(
            object: ApiResponseHandler<InterestViewState, ListGenericDto<Category, CategoryDto>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericDto<Category, CategoryDto>): DataState<InterestViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    if (resultObj.results.isNotEmpty()){
                        val result = authTokenDao.insert(
                            AuthToken(
                                sessionManager.cachedToken.value!!.account_pk,
                                sessionManager.cachedToken.value!!.token,
                                true
                            )
                        )

                        if(result < 0){
                            return DataState.error(
                                response = Response(
                                    ErrorHandling.ERROR_SAVE_AUTH_TOKEN,
                                    UIComponentType.Dialog(),
                                    MessageType.Error()
                                ),
                                stateEvent = stateEvent
                            )
                        }
                    }
                    val categoryList = resultObj.toList()
                    return DataState.data(
                        data = InterestViewState(
                            categoryFields = CategoryFields(
                                userInterestList = categoryList
                            )
                        ),
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_User_Interest_Center,
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
    ): Flow<DataState<InterestViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiInterestService.resolveUserCity(
                country = country,
                city = city
            )
        }
        emit(
            object: ApiResponseHandler<InterestViewState, ListGenericDto<City, CityDto>>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: ListGenericDto<City, CityDto>): DataState<InterestViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    Log.d(TAG,"handleSuccess result ${resultObj}")
                    val cityList = resultObj.toList()
                    return DataState.data(
                        data = InterestViewState(
                            configurationFields = ConfigurationFields(
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

    override fun attemptCreateAddress(
        stateEvent: StateEvent,
        address: Address
    ): Flow<DataState<InterestViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiInterestService.createAddress(
                address = address
            )
        }
        emit(
            object: ApiResponseHandler<InterestViewState,GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<InterestViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_Create_Address,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }

            }.getResult()
        )
    }

    override fun attemptSetUserDefaultCity(
        stateEvent: StateEvent,
        city: City
    ): Flow<DataState<InterestViewState>> = flow {
        val apiResult = safeApiCall(Dispatchers.IO){
            openApiInterestService.setUserDefaultCity(
                city = city
            )
        }
        emit(
            object: ApiResponseHandler<InterestViewState,GenericResponse>(
                response = apiResult,
                stateEvent = stateEvent
            ) {
                override suspend fun handleSuccess(resultObj: GenericResponse): DataState<InterestViewState> {
                    Log.d(TAG,"handleSuccess ${stateEvent}")
                    return DataState.data(
                        data = null,
                        stateEvent = stateEvent,
                        response = Response(
                            SuccessHandling.DONE_User_Default_City,
                            UIComponentType.None(),
                            MessageType.None()
                        )
                    )
                }

            }.getResult()
        )
    }
}