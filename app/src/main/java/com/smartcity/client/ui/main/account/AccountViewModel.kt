package com.smartcity.client.ui.main.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.OrderType
import com.smartcity.client.repository.main.AccountRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Loading
import com.smartcity.client.ui.main.account.state.AccountStateEvent
import com.smartcity.client.ui.main.account.state.AccountStateEvent.*
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.cart.viewmodel.CartViewModel
import com.smartcity.client.util.AbsentLiveData
import javax.inject.Inject

@MainScope
class AccountViewModel
@Inject
constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
)
    : BaseViewModel<AccountStateEvent, AccountViewState>()
{
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

            is None ->{
                return liveData {
                    emit(
                        DataState(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }


    fun getAddressList(): List<Address> {
        getCurrentViewStateOrNew().let {
            return it.addressList
        }
    }

    fun setAddressList(addresses:List<Address>){
        val update = getCurrentViewStateOrNew()
        update.addressList=addresses
        setViewState(update)
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
}














