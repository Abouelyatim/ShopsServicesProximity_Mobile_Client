package com.smartcity.client.repository.main

import com.smartcity.client.di.main.MainScope
import com.smartcity.client.ui.main.flash_notification.state.FlashViewState
import com.smartcity.client.util.DataState
import com.smartcity.client.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow

@FlowPreview
@MainScope
interface FlashRepository {

    fun attemptUserFlashDeals(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<FlashViewState>>
    fun attemptUserDiscountProduct(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<FlashViewState>>
    fun attemptAddProductCart(
        stateEvent: StateEvent,
        userId: Long,
        variantId: Long,
        quantity: Int
    ): Flow<DataState<FlashViewState>>
}