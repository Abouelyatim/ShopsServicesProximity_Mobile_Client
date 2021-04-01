package com.smartcity.client.ui.main.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.product.Cart
import com.smartcity.client.repository.main.CartRepository
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.DataState
import com.smartcity.client.ui.Loading

import com.smartcity.client.ui.main.cart.state.CartStateEvent
import com.smartcity.client.ui.main.cart.state.CartStateEvent.*
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.util.AbsentLiveData

import javax.inject.Inject

@MainScope
class CustomCategoryViewModel
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
                    cartRepository.attemptPlaceOrder(
                        authToken.account_pk!!.toLong(),
                        stateEvent.cart
                    )
                }?: AbsentLiveData.create()
            }

            is None -> {
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

    override fun initNewViewState(): CartViewState {
        return CartViewState()
    }

    fun cancelActiveJobs(){
        cartRepository.cancelActiveJobs()
        handlePendingData()
    }

    fun getCartList(): Cart? {
        getCurrentViewStateOrNew().let {
            return it.cartFields.cartList
        }
    }

    fun setCartList(cart:Cart){
        val update = getCurrentViewStateOrNew()
        update.cartFields.cartList=cart
        setViewState(update)
    }

    fun clearCartList(){
        val update = getCurrentViewStateOrNew()
        update.cartFields.cartList= Cart(listOf(),"")
        setViewState(update)
    }

    fun handlePendingData(){
        setStateEvent(None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }
}










