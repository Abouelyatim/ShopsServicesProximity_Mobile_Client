package com.smartcity.client.ui.main.account.viewmodel

import android.content.SharedPreferences
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.repository.main.AccountRepositoryImpl
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.main.account.state.AccountStateEvent.*
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.util.*
import com.smartcity.client.util.PreferenceKeys.Companion.LOCATION_STORE_AROUND_LATITUDE
import com.smartcity.client.util.PreferenceKeys.Companion.LOCATION_STORE_AROUND_LONGITUDE
import com.smartcity.client.util.PreferenceKeys.Companion.LOCATION_STORE_AROUND_RADIUS
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepositoryImpl,
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
) : BaseViewModel<AccountViewState>()
{
    init {
        setRadius(
            sharedPreferences.getFloat(LOCATION_STORE_AROUND_RADIUS,20.0F).toDouble()
        )
    }

    fun saveLocationInformation(radius :Double){
        editor.putFloat(LOCATION_STORE_AROUND_RADIUS,radius.toFloat())
        editor.apply()
    }

    override fun handleNewData(data: AccountViewState) {
        data.addressFields.let {addressFields ->
            addressFields.addressList?.let {list ->
                setAddressList(list)
            }

            addressFields.defaultCity?.let { city ->
                setDefaultCity(city)
            }
        }

        data.userInformation?.let {infos ->
            setUserInformation(infos)
        }

        data.orderFields.let {orderFields ->
            orderFields.ordersList?.let {list ->
                setOrdersList(list)
            }
        }

        data.aroundStoresFields.let {aroundStoresFields ->
            aroundStoresFields.stores?.let {list ->
                setStoresAround(list)
            }

            aroundStoresFields.cityList?.let {list ->
                setCityList(list)
            }

            aroundStoresFields.searchStores?.let { list ->
                setSearchStores(list)
            }

            aroundStoresFields.categoryList?.let {list ->
                setCategoryList(list)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)){
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<AccountViewState>> = when(stateEvent){

                    is SaveAddressEvent ->{
                        stateEvent.address.userId=authToken.account_pk!!.toLong()
                        accountRepository.attemptCreateAddress(
                            stateEvent,
                            stateEvent.address
                        )
                    }

                    is GetUserAddressesEvent ->{
                        accountRepository.attemptUserAddresses(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is DeleteAddressEvent ->{
                         accountRepository.attemptDeleteAddress(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is SetUserInformationEvent ->{
                        stateEvent.userInformation.userId=authToken.account_pk!!.toLong()
                        accountRepository.attemptSetUserInformation(
                            stateEvent,
                            stateEvent.userInformation
                        )
                    }

                    is GetUserInformationEvent ->{
                        accountRepository.attemptGetUserInformation(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is GetUserInProgressOrdersEvent ->{
                        accountRepository.attemptUserInProgressOrders(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.date,
                            stateEvent.amount,
                            stateEvent.type
                        )
                    }

                    is GetUserFinalizedOrdersEvent ->{
                        accountRepository.attemptUserFinalizedOrders(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.date,
                            stateEvent.amount,
                            stateEvent.type,
                            stateEvent.status
                        )
                    }

                    is ConfirmOrderReceivedEvent ->{
                        accountRepository.attemptConfirmOrderReceived(
                            stateEvent,
                            stateEvent.id
                        )
                    }

                    is GetStoresAroundEvent ->{
                         accountRepository.attemptGetStoreAround(
                            stateEvent,
                             stateEvent.lat,
                             stateEvent.lon,
                             stateEvent.radius
                        )
                    }

                    is SearchStoresAroundEvent ->{
                        accountRepository.attemptSearchStoreAround(
                            stateEvent,
                            stateEvent.lat,
                            stateEvent.lon,
                            stateEvent.radius,
                            stateEvent.category
                        )
                    }

                    is GetUserDefaultCityEvent ->{
                        accountRepository.attemptUserDefaultCity(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is ResolveUserAddressEvent ->{
                        accountRepository.attemptResolveUserAddress(
                            stateEvent,
                            getDefaultCity()!!.country,
                            getCityQuery()
                        )
                    }

                    is AllCategoryEvent ->{
                        accountRepository.attemptAllCategory(
                            stateEvent
                        )
                    }

                    else -> {
                        flow{
                            emit(
                                DataState.error<AccountViewState>(
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

    override fun initNewViewState(): AccountViewState {
        return AccountViewState()
    }
}














