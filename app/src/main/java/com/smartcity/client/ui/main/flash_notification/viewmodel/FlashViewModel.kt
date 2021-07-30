package com.smartcity.client.ui.main.flash_notification.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.repository.main.FlashRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.deleted.BaseViewModel
import com.smartcity.client.ui.deleted.DataState
import com.smartcity.client.ui.deleted.Loading
import com.smartcity.client.ui.main.flash_notification.state.FlashStateEvent
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.util.deleted.AbsentLiveData
import com.smartcity.client.util.PreferenceKeys
import javax.inject.Inject

@MainScope
class FlashViewModel
@Inject
constructor(
    private val flashRepository: FlashRepository,
    private val sessionManager: SessionManager,
    private val editor: SharedPreferences.Editor
): BaseViewModel<FlashStateEvent, FlashViewState>() {

    init {
        saveFlashBadge()
        initRepositoryViewModel()
    }

    private fun saveFlashBadge(){
        editor.putBoolean(PreferenceKeys.NEW_FLASH_NOTIFICATION,false)
        editor.apply()
    }

    override fun handleStateEvent(stateEvent: FlashStateEvent): LiveData<DataState<FlashViewState>> {
        when(stateEvent) {

            is FlashStateEvent.GetUserFlashDealsEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    flashRepository.attemptUserFlashDeals(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is FlashStateEvent.GetUserDiscountProductEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    flashRepository.attemptUserDiscountProduct(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is FlashStateEvent.AddProductCartEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    flashRepository.attemptAddProductCart(
                        authToken.account_pk!!.toLong(),
                        stateEvent.variantId,
                        stateEvent.quantity
                    )
                }?: AbsentLiveData.create()
            }

            is FlashStateEvent.None -> {
                return liveData {
                    emit(
                        DataState<FlashViewState>(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }

    override fun initNewViewState(): FlashViewState {
        return FlashViewState()
    }

    fun cancelActiveJobs(){
        flashRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData(){
        setStateEvent(FlashStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    override fun initRepositoryViewModel() {

    }
}