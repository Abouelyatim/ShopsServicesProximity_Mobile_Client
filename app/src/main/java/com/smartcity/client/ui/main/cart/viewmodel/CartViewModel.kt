package com.smartcity.client.ui.main.cart.viewmodel

import com.smartcity.client.di.main.MainScope
import com.smartcity.client.repository.main.CartRepositoryImpl
import com.smartcity.client.session.SessionManager
import com.smartcity.client.ui.BaseViewModel
import com.smartcity.client.ui.main.cart.state.CartStateEvent.*
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@ExperimentalCoroutinesApi
@FlowPreview
@MainScope
class CartViewModel
@Inject
constructor(
    val cartRepository: CartRepositoryImpl,
    val sessionManager: SessionManager
): BaseViewModel<CartViewState>() {

    override fun handleNewData(data: CartViewState) {
        data.cartFields.let {cartFields ->
            cartFields.cartList?.let { list ->
                setCartList(list)
            }
        }

        data.orderFields.let {orderFields ->
            orderFields.storePolicy?.let {policy ->
                setStorePolicy(policy)
            }

            orderFields.total?.let {total ->
                setTotalBill(total)
            }

            orderFields.addressList?.let { list ->
                setAddressList(list)
            }

            orderFields.defaultCity?.let { city ->
                setDefaultCity(city)
            }

            orderFields.userInformation?.let {userInformation ->
                setUserInformation(userInformation)
            }
        }
    }

    override fun setStateEvent(stateEvent: StateEvent) {
        if(!isJobAlreadyActive(stateEvent)) {
            sessionManager.cachedToken.value?.let { authToken ->
                val job: Flow<DataState<CartViewState>> = when (stateEvent) {

                    is GetUserCartEvent ->{
                        cartRepository.attemptUserCart(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is AddProductCartEvent ->{
                        cartRepository.attemptAddProductCart(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.variantId,
                            stateEvent.quantity
                        )
                    }

                    is DeleteProductCartEvent ->{
                        cartRepository.attemptDeleteProductCart(
                            stateEvent,
                            authToken.account_pk!!.toLong(),
                            stateEvent.variantId
                        )
                    }

                    is PlaceOrderEvent ->{
                        stateEvent.order.userId=authToken.account_pk!!.toLong()
                        cartRepository.attemptPlaceOrder(
                            stateEvent,
                            stateEvent.order
                        )
                    }

                    is GetStorePolicyEvent ->{
                         cartRepository.attemptStorePolicy(
                            stateEvent,
                            stateEvent.storeId
                        )
                    }

                    is GetTotalBillEvent ->{
                         cartRepository.attemptTotalBill(
                            stateEvent,
                            stateEvent.bill
                        )
                    }

                    is GetUserAddressesEvent ->{
                        cartRepository.attemptUserAddresses(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is SaveAddressEvent ->{
                        stateEvent.address.userId=authToken.account_pk!!.toLong()
                        cartRepository.attemptCreateAddress(
                            stateEvent,
                            stateEvent.address
                        )
                    }

                    is GetUserInformationEvent ->{
                        cartRepository.attemptGetUserInformation(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    is GetUserDefaultCityEvent ->{
                        cartRepository.attemptUserDefaultCity(
                            stateEvent,
                            authToken.account_pk!!.toLong()
                        )
                    }

                    else -> {
                        flow{
                            emit(
                                DataState.error<CartViewState>(
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

    override fun initNewViewState(): CartViewState {
        return CartViewState()
    }
}










