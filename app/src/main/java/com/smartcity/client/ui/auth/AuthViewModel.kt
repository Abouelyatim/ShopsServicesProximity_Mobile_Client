package com.smartcity.client.ui.auth

import android.util.Log
import com.smartcity.client.di.auth.AuthScope
import com.smartcity.client.models.AuthToken
import com.smartcity.client.repository.auth.AuthRepositoryImpl
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.auth.state.AuthStateEvent.*
import com.smartcity.client.ui.auth.state.AuthViewState
import com.smartcity.client.ui.auth.state.LoginFields
import com.smartcity.client.ui.auth.state.RegistrationFields
import com.smartcity.client.util.*
import com.smartcity.client.util.ErrorHandling.Companion.INVALID_STATE_EVENT
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@AuthScope
class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepositoryImpl
): BaseViewModel<AuthViewState>() {

    override fun handleNewData(data: AuthViewState) {
        data.authToken?.let { authToken ->
            setAuthToken(authToken)
        }

        data.registrationState.isRegistred?.let {isRegistred ->
            setIsRegistred(isRegistred)
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        val job: Flow<DataState<AuthViewState>> = when(stateEvent){
            is LoginAttemptEvent -> {
                authRepository.attemptLogin(
                    stateEvent,
                    stateEvent.email,
                    stateEvent.password
                )
            }

            is RegisterAttemptEvent -> {
                 authRepository.attemptRegistration(
                     stateEvent,
                    stateEvent.email,
                    stateEvent.username,
                    stateEvent.password,
                    stateEvent.confirm_password
                )
            }

            is CheckPreviousAuthEvent -> {
                 authRepository.checkPreviousAuthUser(stateEvent)
            }

            else -> {
                flow{
                    emit(
                        DataState.error<AuthViewState>(
                            response = Response(
                                message = INVALID_STATE_EVENT,
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

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }

    fun setRegistrationFields(registrationFields: RegistrationFields){
        val update = getCurrentViewStateOrNew()
        if(update.registrationFields == registrationFields){
            return
        }
        update.registrationFields = registrationFields
        setViewState(update)
    }

    fun setLoginFields(loginFields: LoginFields){
        val update = getCurrentViewStateOrNew()
        if(update.loginFields == loginFields){
            return
        }
        update.loginFields = loginFields
        setViewState(update)
    }

    fun setAuthToken(authToken: AuthToken){
        val update = getCurrentViewStateOrNew()
        if(update.authToken == authToken){
            return
        }
        update.authToken = authToken
        setViewState(update)
    }

    fun isRegistred(): Boolean{
        getCurrentViewStateOrNew().let {
            return it.registrationState.isRegistred?:false
        }
    }

    fun setIsRegistred(isRegistred: Boolean){
        val update = getCurrentViewStateOrNew()
        update.registrationState.isRegistred = isRegistred
        setViewState(update)
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}
































