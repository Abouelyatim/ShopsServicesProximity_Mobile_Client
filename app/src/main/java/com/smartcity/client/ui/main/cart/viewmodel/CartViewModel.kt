package com.smartcity.client.ui.main.cart.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.product.Cart
import com.smartcity.client.repository.main.CartRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Loading
import com.smartcity.client.ui.main.account.state.AccountStateEvent

import com.smartcity.client.ui.main.cart.state.CartStateEvent
import com.smartcity.client.ui.main.cart.state.CartStateEvent.*
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.util.AbsentLiveData

import javax.inject.Inject

@MainScope
class CartViewModel
@Inject
constructor(
    val cartRepository: CartRepository,
    val sessionManager: SessionManager
): BaseViewModel<CartStateEvent, CartViewState>() {

    override fun handleStateEvent(stateEvent: CartStateEvent): LiveData<DataState<CartViewState>> {

        when(stateEvent){
            is GetUserCart ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    cartRepository.attemptUserCart(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is AddProductCartEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    cartRepository.attemptAddProductCart(
                        authToken.account_pk!!.toLong(),
                        stateEvent.variantId,
                        stateEvent.quantity
                    )
                }?: AbsentLiveData.create()
            }

            is DeleteProductCartEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    cartRepository.attemptDeleteProductCart(
                        authToken.account_pk!!.toLong(),
                        stateEvent.variantId
                    )
                }?: AbsentLiveData.create()
            }

            is PlaceOrderEvent ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.order.userId=authToken.account_pk!!.toLong()
                    cartRepository.attemptPlaceOrder(
                        stateEvent.order
                    )
                }?: AbsentLiveData.create()
            }

            is GetStorePolicy ->{
                return cartRepository.attemptStorePolicy(
                    stateEvent.storeId
                )
            }

            is GetTotalBill ->{
                return cartRepository.attemptTotalBill(
                    stateEvent.bill
                )
            }

            is GetUserAddresses ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    cartRepository.attemptUserAddresses(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is SaveAddress ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    stateEvent.address.userId=authToken.account_pk!!.toLong()
                    cartRepository.attemptCreateAddress(
                        stateEvent.address
                    )
                }?: AbsentLiveData.create()
            }

            is GetUserInformation ->{
                return sessionManager.cachedToken.value?.let { authToken ->
                    cartRepository.attemptGetUserInformation(
                        authToken.account_pk!!.toLong()
                    )
                }?: AbsentLiveData.create()
            }

            is None -> {
                return liveData {
                    emit(
                        DataState<CartViewState>(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }

    }

    override fun initNewViewState(): CartViewState {
        return CartViewState()
    }

    fun cancelActiveJobs(){
        cartRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }

    override fun initRepositoryViewModel() {

    }
}










