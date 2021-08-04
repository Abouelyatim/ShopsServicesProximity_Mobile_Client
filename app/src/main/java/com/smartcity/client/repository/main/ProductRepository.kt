package com.smartcity.client.repository.main

import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.product.state.ProductViewState
import com.smartcity.client.util.DataState
import com.smartcity.client.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@MainScope
interface ProductRepository {

    fun attemptSearchProducts(
        stateEvent: StateEvent,
        query: String,
        page: Int,
        userId: Long
    ): Flow<DataState<ProductViewState>>

    fun attemptAllProducts(
        stateEvent: StateEvent,
        page: Int,
        userId: Long
    ): Flow<DataState<ProductViewState>>

    fun attemptAddProductCart(
        stateEvent: StateEvent,
        userId: Long,
        variantId: Long,
        quantity: Int
    ): Flow<DataState<ProductViewState>>
    
    fun attemptGetStoreCustomCategory(
        stateEvent: StateEvent,
        storeId:Long
    ): Flow<DataState<ProductViewState>>
    
    fun attemptGetProductsByCustomCategory(
        stateEvent: StateEvent,
        customCategoryId:Long
    ): Flow<DataState<ProductViewState>>
    
    fun attemptGetAllProductsByStore(
        stateEvent: StateEvent,
        storeId:Long
    ): Flow<DataState<ProductViewState>>
    
    fun attemptFollowStore(
        stateEvent: StateEvent,
        id: Long,
        idUser: Long
    ): Flow<DataState<ProductViewState>>
    
    fun attemptStopFollowingStore(
        stateEvent: StateEvent,
        id: Long,
        idUser: Long
    ): Flow<DataState<ProductViewState>>
    
    fun attemptIsFollowingStore(
        stateEvent: StateEvent,
        id: Long,
        idUser: Long
    ): Flow<DataState<ProductViewState>>
    
    fun attemptInterestProduct(
        stateEvent: StateEvent,
        page: Int,
        userId: Long
    ): Flow<DataState<ProductViewState>>
}