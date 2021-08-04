package com.smartcity.client.repository.main

import com.smartcity.client.di.main.MainScope
import com.smartcity.client.models.Address
import com.smartcity.client.models.UserInformation
import com.smartcity.client.ui.main.account.state.AccountViewState
import com.smartcity.client.util.DataState
import com.smartcity.client.util.StateEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow


@FlowPreview
@MainScope
interface AccountRepository {

    fun attemptCreateAddress(
        stateEvent: StateEvent,
        address: Address
    ): Flow<DataState<AccountViewState>>

    fun attemptUserAddresses(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>>

    fun attemptDeleteAddress(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>>

    fun attemptSetUserInformation(
        stateEvent: StateEvent,
        userInformation: UserInformation
    ): Flow<DataState<AccountViewState>>

    fun attemptGetUserInformation(
        stateEvent: StateEvent,
        id:Long
    ): Flow<DataState<AccountViewState>>

    fun attemptUserInProgressOrders(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>>

    fun attemptUserFinalizedOrders(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>>

    fun attemptConfirmOrderReceived(
        stateEvent: StateEvent,
        id:Long
    ): Flow<DataState<AccountViewState>>

    fun attemptGetStoreAround(
        stateEvent: StateEvent,
        centerLatitude:Double,
        centerLongitude:Double,
        radius:Double
    ): Flow<DataState<AccountViewState>>

    fun attemptUserDefaultCity(
        stateEvent: StateEvent,
        id: Long
    ): Flow<DataState<AccountViewState>>
}