package com.smartcity.client.ui.main.flash_notification.viewmodel

import android.content.SharedPreferences
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.repository.main.FlashRepositoryImpl
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.main.flash_notification.state.FlashStateEvent
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class FlashViewModel
@Inject
constructor(
    private val flashRepository: FlashRepositoryImpl,
    private val sessionManager: SessionManager,
    private val editor: SharedPreferences.Editor
): BaseViewModel<FlashViewState>() {

    init {
        saveFlashBadge()
    }

    private fun saveFlashBadge(){
        editor.putBoolean(PreferenceKeys.NEW_FLASH_NOTIFICATION,false)
        editor.apply()
    }

    override fun handleNewData(data: FlashViewState) {
        data.flashFields.let { flashFields ->
            flashFields.networkFlashDealsPair?.let {pair ->
                setFlashDealsList(pair)
            }

            flashFields.productDiscountList?.let { list ->
                setDiscountProductList(list)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)){
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<FlashViewState>> = when(stateEvent){

                    is FlashStateEvent.GetUserFlashDealsEvent ->{
                        flashRepository.attemptUserFlashDeals(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.date
                        )
                    }

                    is FlashStateEvent.GetUserDiscountProductEvent ->{
                        flashRepository.attemptUserDiscountProduct(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is FlashStateEvent.AddProductCartEvent ->{
                        flashRepository.attemptAddProductCart(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.variantId,
                            stateEvent.quantity
                        )
                    }
                    else -> {
                        flow{
                            emit(
                                DataState.error<FlashViewState>(
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

    override fun initNewViewState(): FlashViewState {
        return FlashViewState()
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}