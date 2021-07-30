package com.smartcity.client.ui.main.account.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.repository.main.AccountRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.deleted.BaseViewModel
import com.smartcity.client.ui.deleted.DataState
import com.smartcity.client.ui.deleted.Loading
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountStateEvent.*
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.util.deleted.AbsentLiveData
import com.smartcity.client.util.PreferenceKeys.Companion.LOCATION_STORE_AROUND_LATITUDE
import com.smartcity.client.util.PreferenceKeys.Companion.LOCATION_STORE_AROUND_LONGITUDE
import com.smartcity.client.util.PreferenceKeys.Companion.LOCATION_STORE_AROUND_RADIUS
import javax.inject.Inject

@MainScope
class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
)
    : BaseViewModel<AccountStateEvent, AccountViewState>()
{
    init {
        initRepositoryViewModel()

        setCenterLatitude(
            sharedPreferences.getFloat(LOCATION_STORE_AROUND_LATITUDE,0.0F).toDouble()
        )

        setCenterLongitude(
            sharedPreferences.getFloat(LOCATION_STORE_AROUND_LONGITUDE,0.0F).toDouble()
        )

        setRadius(
            sharedPreferences.getFloat(LOCATION_STORE_AROUND_RADIUS,20.0F).toDouble()
        )
    }

    fun saveLocationInformation(centerLatitude: Double, centerLongitude: Double,radius :Double){
        editor.putFloat(LOCATION_STORE_AROUND_LATITUDE,centerLatitude.toFloat())
        editor.putFloat(LOCATION_STORE_AROUND_LONGITUDE,centerLongitude.toFloat())
        editor.putFloat(LOCATION_STORE_AROUND_RADIUS,radius.toFloat())
        editor.apply()
    }

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent){

            is SaveAddress ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.address.userId=authToken.account_pk!!.toLong()
                    accountRepository.attemptCreateAddress(
                        stateEvent.address
                    )
                }?: AbsentLiveData.create()
            }

            is GetUserAddresses ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.attemptUserAddresses(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is DeleteAddress ->{
                return accountRepository.attemptDeleteAddress(
                    stateEvent.id
                )
            }

            is SetUserInformation ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.userInformation.userId=authToken.account_pk!!.toLong()
                    accountRepository.attemptSetUserInformation(
                        stateEvent.userInformation
                    )

                }?: AbsentLiveData.create()
            }

            is GetUserInformation ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.attemptGetUserInformation(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is GetUserInProgressOrdersEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.attemptUserInProgressOrders(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is GetUserFinalizedOrdersEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.attemptUserFinalizedOrders(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is ConfirmOrderReceivedEvent ->{
                return accountRepository.attemptConfirmOrderReceived(
                    stateEvent.id
                )
            }

            is SubscribeOrderChangeEvent ->{
                return accountRepository.attemptSubscribeOrderChangeEvent()
            }
            is ResponseOrderChangeEvent ->{
                return accountRepository.attemptResponseOrderChangeEvent()
            }
            is FinishOrderChangeEvent ->{
                return accountRepository.attemptFinishOrderChangeEvent()
            }

            is GetStoresAround ->{
                return accountRepository.attemptGetStoreAround(
                    getCenterLatitude(),
                    getCenterLongitude(),
                    getRadius()
                )
            }

            is None ->{
                return liveData {
                    emit(
                        DataState<AccountViewState>(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }

    fun cancelActiveJobs(){
        accountRepository.cancelActiveJobs() // cancel active jobs
        handlePendingData() // hide progress bar
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    override fun initRepositoryViewModel() {
        accountRepository.setCurrentViewModel(this)
    }
}














