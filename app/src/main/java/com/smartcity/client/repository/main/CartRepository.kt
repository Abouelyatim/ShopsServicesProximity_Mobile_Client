package com.smartcity.client.repository.main

import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.BillTotal
import com.smartcity.client.models.Order
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.ui.main.cart.state.CartViewState
import com.smartcity.client.util.DataState
import com.smartcity.client.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@MainScope
interface CartRepository {

    fun attemptUserCart(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CartViewState>>
    
    fun attemptAddProductCart(
        stateEvent: StateEvent,
        userId: Long,
        variantId: Long,
        quantity: Int
    ): Flow<DataState<CartViewState>>
    
    fun attemptDeleteProductCart(
        stateEvent: StateEvent,
        userId: Long,
        variantId: Long
    ): Flow<DataState<CartViewState>>
    
    fun attemptPlaceOrder(
        stateEvent: StateEvent,
        order: Order
    ): Flow<DataState<CartViewState>>
    
    fun attemptStorePolicy(
        stateEvent: StateEvent,
        storeId: Long
    ): Flow<DataState<CartViewState>>
    
    fun attemptTotalBill(
        stateEvent: StateEvent,
        bill: BillTotal
    ): Flow<DataState<CartViewState>>
    
    fun attemptUserAddresses(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CartViewState>>
    
    fun attemptCreateAddress(
        stateEvent: StateEvent,
        address: Address
    ): Flow<DataState<CartViewState>>
    
    fun attemptGetUserInformation(
        stateEvent: StateEvent,
        id:Long
    ): Flow<DataState<CartViewState>>

    fun attemptUserDefaultCity(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<CartViewState>>
}