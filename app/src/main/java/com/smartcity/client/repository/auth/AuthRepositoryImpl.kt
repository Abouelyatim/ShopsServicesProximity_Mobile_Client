package com.smartcity.client.repository.auth

import android.content.SharedPreferences
import android.util.Log
import com.smartcity.client.api.auth.OpenApiAuthService
import com.smartcity.client.api.auth.network_responses.LoginResponse
import com.smartcity.client.api.auth.network_responses.RegistrationResponse
import com.smartcity.client.di.auth.AuthScope
import com.smartcity.client.models.AccountProperties
import com.smartcity.client.models.AuthToken
import com.smartcity.client.persistence.AccountPropertiesDao
import com.smartcity.client.persistence.AuthTokenDao
import com.smartcity.client.repository.buildError
import com.smartcity.client.repository.safeApiCall
import com.smartcity.client.repository.safeCacheCall
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.auth.state.AuthViewState
import com.smartcity.client.ui.auth.state.LoginFields
import com.smartcity.client.ui.auth.state.RegistrationFields
import com.smartcity.client.ui.auth.state.RegistrationState
import com.smartcity.client.util.*
import com.smartcity.client.util.ErrorHandling.Companion.ERROR_SAVE_ACCOUNT_PROPERTIES
import com.smartcity.client.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.smartcity.client.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.smartcity.client.util.ErrorHandling.Companion.INVALID_CREDENTIALS
import com.smartcity.client.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@FlowPreview
@AuthScope
class AuthRepositoryImpl
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
): AuthRepository
{

    private val TAG: String = "AppDebug"

    override fun attemptLogin(
        stateEvent: StateEvent,
        email: String,
        password: String
    ): Flow<DataState<AuthViewState>> = flow {
        val loginFieldErrors = LoginFields(email, password).isValidForLogin()

        if(loginFieldErrors.equals(LoginFields.LoginError.none())){
            val apiResult = safeApiCall(IO){
                openApiAuthService.login(email, password)
            }
            emit(
                object: ApiResponseHandler<AuthViewState, LoginResponse>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: LoginResponse): DataState<AuthViewState> {
                        Log.d(TAG, "handleSuccess ")

                        // Incorrect login credentials counts as a 200 response from server, so need to handle that
                        if(resultObj.response.equals(GENERIC_AUTH_ERROR)){
                            return DataState.error(
                                response = Response(
                                    INVALID_CREDENTIALS,
                                    UIComponentType.Dialog(),
                                    MessageType.Error()
                                ),
                                stateEvent = stateEvent
                            )
                        }

                        accountPropertiesDao.insertOrIgnore(
                            AccountProperties(
                                resultObj.pk,
                                resultObj.email,
                                ""
                            )
                        )

                        // will return -1 if failure
                        val authToken = AuthToken(
                            resultObj.pk,
                            resultObj.token
                        )

                        val result = authTokenDao.insert(
                            AuthToken(
                                resultObj.pk,
                                resultObj.token,
                                false
                            )
                        )

                        if(result < 0){
                            return DataState.error(
                                response = Response(
                                    ERROR_SAVE_AUTH_TOKEN,
                                    UIComponentType.Dialog(),
                                    MessageType.Error()
                                ),
                                stateEvent = stateEvent
                            )
                        }

                        saveAuthenticatedUserToPrefs(email)

                        return DataState.data(
                            data = AuthViewState(
                                authToken = authToken
                            ),
                            stateEvent = stateEvent,
                            response = null
                        )
                    }

                }.getResult()
            )
        }else{
            Log.d(TAG, "emitting error: ${loginFieldErrors}")
            emit(
                buildError<AuthViewState>(
                    loginFieldErrors,
                    UIComponentType.Dialog(),
                    stateEvent
                )
            )
        }
    }

    override fun attemptRegistration(
        stateEvent: StateEvent,
        email: String,
        username: String,
        password: String,
        confirmPassword: String
    ): Flow<DataState<AuthViewState>> = flow {
        val registrationFieldErrors = RegistrationFields(email, username, password, confirmPassword).isValidForRegistration()

        if(registrationFieldErrors.equals(LoginFields.LoginError.none())){
            val apiResult = safeApiCall(IO){
                openApiAuthService.register(
                    email,
                    username,
                    password,
                    confirmPassword
                )
            }

            emit(
                object: ApiResponseHandler<AuthViewState, RegistrationResponse>(
                    response = apiResult,
                    stateEvent = stateEvent
                ) {
                    override suspend fun handleSuccess(resultObj: RegistrationResponse): DataState<AuthViewState> {
                        if(resultObj.response.equals(GENERIC_AUTH_ERROR)){
                            return DataState.error(
                                response = Response(
                                    resultObj.response,
                                    UIComponentType.Dialog(),
                                    MessageType.Error()
                                ),
                                stateEvent = stateEvent
                            )
                        }

                        val result1 = accountPropertiesDao.insertAndReplace(
                            AccountProperties(
                                resultObj.pk,
                                resultObj.email,
                                ""
                            )
                        )

                        // will return -1 if failure
                        if(result1 < 0){
                            return DataState.error(
                                response = Response(
                                    ERROR_SAVE_ACCOUNT_PROPERTIES,
                                    UIComponentType.Dialog(),
                                    MessageType.Error()
                                ),
                                stateEvent = stateEvent
                            )
                        }

                        // will return -1 if failure
                        val authToken = AuthToken(
                            resultObj.pk,
                            null,
                            false
                        )

                        val result = authTokenDao.insert(authToken)

                        if(result < 0){
                            return DataState.error(
                                response = Response(
                                    ERROR_SAVE_AUTH_TOKEN,
                                    UIComponentType.Dialog(),
                                    MessageType.Error()
                                ),
                                stateEvent = stateEvent
                            )
                        }
                        saveAuthenticatedUserToPrefs(email)

                        return DataState.data(
                            data = AuthViewState(
                                authToken = authToken,
                                registrationState= RegistrationState(
                                    isRegistred = true
                                )
                            ),
                            stateEvent = stateEvent,
                            response = null
                        )
                    }

                }.getResult()
            )
        }else{
            emit(
                buildError<AuthViewState>(
                    registrationFieldErrors,
                    UIComponentType.Dialog(),
                    stateEvent
                )
            )
        }
    }

    override fun checkPreviousAuthUser(
        stateEvent: StateEvent
    ): Flow<DataState<AuthViewState>> = flow {
        val previousAuthUserEmail: String? = sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)

        if(previousAuthUserEmail.isNullOrBlank()){
            Log.d(TAG, "checkPreviousAuthUser: No previously authenticated user found.")
            emit(returnNoTokenFound(stateEvent))
        }else{
            val apiResult = safeCacheCall(IO){
                accountPropertiesDao.searchByEmail(previousAuthUserEmail)
            }

            emit(
                object: CacheResponseHandler<AuthViewState, AccountProperties>(
                    response = apiResult,
                    stateEvent = stateEvent
                ){
                    override suspend fun handleSuccess(resultObj: AccountProperties): DataState<AuthViewState> {
                        if(resultObj.pk > -1){
                            authTokenDao.searchByPk(resultObj.pk).let { authToken ->
                                if(authToken != null){
                                    if(authToken.token != null){
                                        return DataState.data(
                                            data = AuthViewState(
                                                authToken = authToken
                                            ),
                                            response = null,
                                            stateEvent = stateEvent
                                        )
                                    }
                                }
                            }
                        }
                        Log.d(TAG, "createCacheRequestAndReturn: AuthToken not found...")
                        return DataState.error(
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                UIComponentType.None(),
                                MessageType.Error()
                            ),
                            stateEvent = stateEvent
                        )
                    }

                }.getResult()
            )
        }
    }

    override fun saveAuthenticatedUserToPrefs(email: String){
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }

    override fun returnNoTokenFound(
        stateEvent: StateEvent
    ): DataState<AuthViewState> {
        return DataState.error(
            response = Response(
                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                UIComponentType.None(),
                MessageType.Error()
            ),
            stateEvent = stateEvent
        )
    }
}







